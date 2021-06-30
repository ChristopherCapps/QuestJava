package com.quest.endpoint.cloud.ws.config;

import com.quest.baldydash.BaldyDash2021;
import com.quest.endpoint.cloud.ws.io.TestIOService;
import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.engine.command.*;
import com.quest.engine.command.privileged.*;
import com.quest.io.Message;
import com.quest.model.Quest;
import com.quest.model.Waypoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class QuestConfig {

//    @Bean
//    public IOService ioService() {
//        return new TestIOService();
//    }

    @Bean Quest quest() {
        return BaldyDash2021.BALDY_DASH_QUEST;
    }

    @Bean
    public Command restartCommand() {
        return new RestartCommand();
    }

    @Bean
    public Command startCommand() {
        return new StartCommand();
    }

    @Bean
    public Command clueCommand() {
        return new ClueCommand();
    }

    @Bean
    public Command crewCommand() {
        return new CrewCommand();
    }

    @Bean
    public Command gmCommand() {
        return new GameMasterCommand();
    }

//    @Bean
//    public Command helpCommand(final CommandRegistry registry) {
//        return new HelpCommand(registry);
//    }

    @Bean
    public Command joinCommand() {
        return new JoinCommand();
    }

    @Bean
    public Command nameCommand() {
        return new NameCommand();
    }

    @Bean
    public Command renameCrewCommand() {
        return new RenameCrewCommand();
    }

    @Bean
    public Command removeCommand() {
        return new RemoveCommand();
    }

    @Bean
    public Command statusCommand() {
        return new StatusCommand();
    }

    @Bean
    public Command tauntCommand() {
        return new TauntCommand();
    }

    @Bean
    public Command courseCommand() { return new CourseCommand(); }

    @Bean
    public Command setWaypointCommand() { return new SetWaypointCommand(); }

    @Bean
    public Command hintCommand() { return new HintCommand(); }

    @Bean
    public CommandRegistry commandRegistry(final List<Command> commands) {
        return new CommandRegistry(commands);
    }

    @Bean
    public Engine engine(final CommandRegistry registry, final Quest quest, final IOService ioService) {
        return Engine.of(registry, quest, ioService, new AnswerCommand());
    }

}
