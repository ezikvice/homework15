package ru.ezikvice.springotus.homework15;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.MessageChannel;
import ru.ezikvice.springotus.homework15.domain.Parcel;

@SpringBootApplication
public class Homework15Application {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(Homework15Application.class);
        Shipping shipping = ctx.getBean(Shipping.class);

        Parcel parcel1 = new Parcel(12.5, 42, "to grandpa in country");

        while (true) {
            Thread.sleep(2000);
            Parcel receivedParcel = shipping.sendParcel(parcel1);
        }
    }


    @Bean
    public IntegrationFlow sendingFlow() {
        return IntegrationFlows
                .from("parcelsChannel")
                .channel("secondChannel")
                .channel("shopToCustomsChannel")
                .bridge(e -> e.poller(Pollers.fixedRate(200).maxMessagesPerPoll(1)))
                .handle("postService", "reportReceiving")
                .get();
    }


    @Bean
    MessageChannel shopToCustomsChannel() {
        return MessageChannels.queue(10_000).get();
    }

    @Bean
    public DirectChannel parcelsChannel() {
        return MessageChannels.direct().datatype(Parcel.class).get();
    }

    @Bean
    DirectChannel secondChannel() {
        return MessageChannels.direct("shopToCustomsChannel").get();
    }
}
