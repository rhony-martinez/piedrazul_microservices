package com.piedrazul.citas.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.citas}")
    private String citasExchange;

    @Value("${rabbitmq.exchange.personas}")
    private String personasExchange;

    // Routing Keys para eventos de citas
    @Value("${rabbitmq.routing.cita-agendada}")
    private String routingKeyCitaAgendada;

    @Value("${rabbitmq.routing.cita-cancelada}")
    private String routingKeyCitaCancelada;

    @Value("${rabbitmq.routing.cita-reagendada}")
    private String routingKeyCitaReagendada;

    // Queues para consumir eventos de personas-service
    @Value("${rabbitmq.queue.paciente-creado}")
    private String queuePacienteCreado;

    @Value("${rabbitmq.queue.medico-creado}")
    private String queueMedicoCreado;

    @Value("${rabbitmq.queue.medico-actualizado}")
    private String queueMedicoActualizado;

    @Value("${rabbitmq.queue.disponibilidad-actualizada}")
    private String queueDisponibilidadActualizada;

    // 1. Configuración de Exchanges
    @Bean
    public TopicExchange citasExchange() {
        return new TopicExchange(citasExchange);
    }

    @Bean
    public TopicExchange personasExchange() {
        return new TopicExchange(personasExchange);
    }

    // 2. Configuración de Queues para consumir eventos de personas-service
    @Bean
    public Queue pacienteCreadoQueue() {
        return new Queue(queuePacienteCreado, true);
    }

    @Bean
    public Queue medicoCreadoQueue() {
        return new Queue(queueMedicoCreado, true);
    }

    @Bean
    public Queue medicoActualizadoQueue() {
        return new Queue(queueMedicoActualizado, true);
    }

    @Bean
    public Queue disponibilidadActualizadaQueue() {
        return new Queue(queueDisponibilidadActualizada, true);
    }

    // 3. Binding de Queues a Exchanges
    @Bean
    public Binding pacienteCreadoBinding() {
        return BindingBuilder
                .bind(pacienteCreadoQueue())
                .to(personasExchange())
                .with("paciente.creado");
    }

    @Bean
    public Binding medicoCreadoBinding() {
        return BindingBuilder
                .bind(medicoCreadoQueue())
                .to(personasExchange())
                .with("medico.creado");
    }

    @Bean
    public Binding medicoActualizadoBinding() {
        return BindingBuilder
                .bind(medicoActualizadoQueue())
                .to(personasExchange())
                .with("medico.actualizado");
    }

    @Bean
    public Binding disponibilidadActualizadaBinding() {
        return BindingBuilder
                .bind(disponibilidadActualizadaQueue())
                .to(personasExchange())
                .with("disponibilidad.actualizada");
    }

    // 4. Configuración de Message Converter para JSON
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 5. Configuración de RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}