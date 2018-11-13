package ru.ezikvice.springotus.homework15;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.aggregator.DefaultAggregatingMessageGroupProcessor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.scheduling.PollerMetadata;
import ru.ezikvice.springotus.homework15.domain.Parcel;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Homework15Application {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(Homework15Application.class);
        Shipping shipping = ctx.getBean(Shipping.class);

        List<Parcel> parcels = Arrays.asList(
                new Parcel(1.5, 1, "a new smartphone", false),
                new Parcel(3.0, 2, "little dog", false),
                new Parcel(8.0, 3, "big dog", false),
                new Parcel(4.0, 4, "other dog", false),
                new Parcel(1.7, 5, "little cat", false),
                new Parcel(1.7, 6, "other little cat", false),
                new Parcel(0.1, 0, "a letter to grandpa in country", true)
        );

        for (Parcel parcel : parcels) {
            shipping.sendParcel(parcel);
        }
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata poller() {
        return Pollers.fixedDelay(1000).get();
    }

    @Bean
    public IntegrationFlow sendingFlow() {
        return

                IntegrationFlows
                        .from("parcelsChannel")
                        .<Parcel, Boolean>route(
                                Parcel::isUrgent,
                                mapping -> mapping
                                        .subFlowMapping(true, sf -> sf
                                                .handle("postService", "acceptParcel")
                                                .handle("postService", "reportSending")
                                        )
                                        .subFlowMapping(false, sf -> sf
                                                        .handle("postService", "acceptParcel")
                                                        // "..За время пути
//                                              // Собачка могла подрасти.."
                                                        .<Parcel, Parcel>transform(p -> {
                                                            String description = p.getDescription();
                                                            if (description.contains("little dog")) {
                                                                p.setWeight(p.getWeight() * 4);
                                                                p.setDescription(description.replace("little", "bigger"));
                                                            }
                                                            return p;
                                                        })
                                                        .aggregate(aggregator -> aggregator
                                                                .outputProcessor(new DefaultAggregatingMessageGroupProcessor())
                                                                .correlationStrategy(m -> 42)
                                                                .releaseStrategy(group -> group.getMessages().size() >= 6)
                                                        )
                                                        .handle("postService", "reportReceivingList")
                                        )
                        )
                        .get()
                ;
    }

    @Bean
    public DirectChannel parcelsChannel() {
        return MessageChannels.direct().datatype(Parcel.class).get();
    }

}
