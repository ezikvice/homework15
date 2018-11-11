package ru.ezikvice.springotus.homework15;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.CorrelationStrategy;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.stereotype.Component;
import ru.ezikvice.springotus.homework15.domain.Parcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class Homework15Application {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(Homework15Application.class);
        Shipping shipping = ctx.getBean(Shipping.class);

        List<Parcel> parcels = Arrays.asList(
                new Parcel(1.5, 1, "a new smartphone", false),
                new Parcel(2.7, 2, "little dog", false),
                new Parcel(17.0, 3, "big dog", false),
                new Parcel(8.0, 4, "other dog", false),
                new Parcel(1.7, 5, "little cat", false),
                new Parcel(0.1, 0, "a letter to grandpa in country", true)
        );

        for (Parcel parcel : parcels) {
            Parcel receivedParcel = shipping.sendParcel(parcel);
            System.out.println("====== " + receivedParcel.toString());
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
                                                .aggregate(aggregator -> aggregator
                                                        .outputProcessor(g ->
                                                                new ArrayList(g.getMessages()
                                                                        .stream()
                                                                        .map(message -> (Parcel) message.getPayload())
                                                                        .collect(Collectors.toList())))
                                                        .correlationStrategy(m -> 42)

                                                )
                                                .handle("postService", "reportSending")
                                        )
                        )
                        .get()
                ;
    }

    @Bean
    public DirectChannel parcelsChannel() {
        return MessageChannels.direct().datatype(Parcel.class).get();
    }


//    @Component
//    public static class ParcelAggregator {
//        @Aggregator
//        public List<Parcel>output(Parcel parcel) {
//            return ;
//        }
//
//        @CorrelationStrategy
//        public Integer correlation(Parcel parcel) {
//            return 42;
//        }
//    }


}
