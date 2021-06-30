package com.quest.engine;

import com.quest.baldydash.BaldyDash2021;
import com.quest.engine.command.*;
import com.quest.engine.command.privileged.CourseCommand;
import com.quest.engine.command.privileged.RestartCommand;
import com.quest.engine.command.privileged.StartCommand;
import com.quest.engine.command.privileged.StatusCommand;
import com.quest.io.Message;
import com.quest.io.TestIOService;
import com.quest.model.Quest;
import com.quest.model.Waypoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class TestCLI {

    private static final Quest baldyDash = BaldyDash2021.BALDY_DASH_QUEST;

    public static void main(String[] args) throws Exception {
        List<Command> commands = new LinkedList<>();
        commands.add(new RestartCommand());
        commands.add(new StartCommand());
        commands.add(new StatusCommand());
        commands.add(new ClueCommand());
        commands.add(new CrewCommand());
        commands.add(new RenameCrewCommand());
        commands.add(new GameMasterCommand());
        commands.add(new JoinCommand());
        commands.add(new NameCommand());
        commands.add(new TauntCommand());
        commands.add(new CourseCommand());
        final CommandRegistry registry = new CommandRegistry(commands);
        new HelpCommand(registry);

        final IOService ioService = new TestIOService();
        final Engine engine = new Engine(registry, baldyDash, ioService, new AnswerCommand());

        final BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            final String input = consoleReader.readLine();
            final int channelDelim = input.indexOf(":");
            final String channel = channelDelim > 0 ? input.substring(0, channelDelim) : "0";
            final String command = channelDelim > 0 ? input.substring(channelDelim+1) : input;
            System.out.println("[" + channel + ":" + command + "]");
            engine.onMessageReceived(channel, command).map(Message::text).ifPresent(System.out::println);
        }
    }
}
