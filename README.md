# Discord Interaction Handler

![Maven Central](https://img.shields.io/maven-central/v/net.fellbaum/dih?label=Discord%20Interaction%20handler)

![Supported Java Version](https://img.shields.io/badge/Supported%20Java%20Version-17%2B-brightgreen)

Discord Interaction Handler is a library for handling any kind of interaction that you can receive from Discord.
The issue is that Discord has a lot of different interaction types, and it's hard to handle them all. This library will
help you with the management of slash commands and responding to any interaction in an easy way.

## Supported Discord libraries

- [Javacord](https://github.com/Javacord/Javacord)

## Getting Started / Examples

### The InteractionHandler

The interaction handler is where everything comes together.
After you have created your files of components and commands, you have to add them to the handler first.

Then you just need to add the listeners and execute `InteractionHandler#bulkOverwriteGlobalApplicationCommands` to
register your commands at Discord.

````java
public class Main {
    public static void main(String[] args) {
        DiscordApi api = new DiscordApiBuilder()
                .setToken("TOKEN")
                .setAllIntents()
                .login().join();

        SimpleInteractionHandler simpleInteractionHandler = new SimpleInteractionHandler();

        simpleInteractionHandler.registerInteraction(new ModerationCommands());
        simpleInteractionHandler.registerInteraction(new NextPageButton());

        simpleInteractionHandler.attachListeners(api);
        //Exchange the commands on Discord with your currently to the interaction handler added ones
        simpleInteractionHandler.bulkOverwriteGlobalApplicationCommands(api);
    }
}
````

After that you are ready receive and handle the interactions based on what you have registered on
the `InteractionHandler`.

#### SimpleInteractionHandler / ComplexInteractionHandler

There are 2 kind of interaction handlers. The `SimpleInteractionHandler` and the `ComplexInteractionHandler`.
They differ in how the interaction handler handles the application command interactions you register on it
(SlashCommand, ContextMenus) but Message components are not affected by this in any way.

In most cases you will want to use the `SimpleInteractionHandler`, because it is sufficient for most use cases.
The only difference between them is how Server application commands are stored and resolved. This means when using
the ``SimpleInteractionHandler``
you can have only 1 specific server application command with the same name.
When using the ``ComplexInteractionHandler`` you always have to provide a server together with your application command instances.
Then these application commands will be bound to this server which allows you to have multiple server application
commands with the same name for different servers. This can be useful if you have a way for users to define their own
commands on their server.

TL;DR use:

- ``SimpleInteractionHandler``: If you only have one server application command with the same name you register on servers, that want to
  use this command.
- ``ComplexInteractionHandler``: If you have multiple server application commands with the same name, but they are different for each (or
  some) server.

### Creating a Slash Command:

````java
public class ModerationCommands extends GlobalSlashCommand {

    private static final String COMMAND_NAME = "moderation";
    private static final String BAN = "ban";

    public ModerationCommands() {
        super(COMMAND_NAME, "Moderation commands");
        getApplicationCommandBuilder()
                .setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR)
                .setEnabledInDms(false)
                .addOption(
                        SlashCommandOption.createWithOptions(
                                SlashCommandOptionType.SUB_COMMAND,
                                BAN,
                                "Ban a user",
                                List.of(SlashCommandOption.createUserOption("user", "The user to ban", true))
                        )
                );
    }

    @Override
    public void runCommand(SlashCommandInteraction interaction) {
        switch (interaction.getFullCommandName().split(" ")[1]) {
            case BAN -> banUser(interaction);
            default -> System.out.println("Unknown subcommand");
        }
    }

    private void banUser(SlashCommandInteraction interaction) {
        User user = interaction.getArguments().get(0).getUserValue().get();
        Server server = interaction.getServer().get();

        server.banUser(user).exceptionally(ExceptionLogger.get());
    }
}
````

#### AutoCompleteInteraction

If you want to receive `AutoCompleteInteraction`s, you have to override the `autocompletionHandler` method when
extending a `GlobalSlashCommand / ServerSlashCommand`. You can handle the AutoCompleteInteraction the same way as you
would handle a SlashCommandInteraction, by checking the name of the option and executing the corresponding code.

### Listening for component interactions:

````java
public class NextPageButton extends ButtonComponent {

    private static final String ID = "next_page_button_";

    public NextPageButton() {
        super(ID + "\\d+");
    }

    @Override
    public void runButtonComponent(ButtonInteraction interaction) {
        String stringNumber = interaction.getCustomId().split(ID)[1];
        int number = Integer.parseInt(stringNumber);
        //TODO: do something with the current page number
    }
}
````

The custom ID will internally be converted to a **RegEx**. In cases where the ID transmits some data like in the example
shown above, hard coded String do not work.
To overcome this issue the receiving component ID from the interaction will be matched against the Custom ID RegEx.
If multiple component are matched when you receive a button interaction it will show you a warning and no component will
be executed.

### Context menu commands

It is pretty straight forward to use context menu commands. Simply extend ``MessageContextMenuCommand``
or ``UserContextMenuCommand`` to register and listen for a context menu command.

````java
public class ExampleMessageContextmenuCommand extends MessageContextMenuCommand {
    public TestMessageContextMenuCommand() {
        super("My Message Command", false);
    }

    @Override
    public void runCommand(final MessageContextMenuInteraction messageContextMenuInteraction) {
    }
}
````

### Using with ``Spring``

If you are using `Spring` you can annotate all your components and commands with `@Component`.
With a new Bean you can easily gather the instances of all your components and commands and register them to
the `SimpleInteractionHandler` / `ComplexInteractionHandler`.
After that you just need to inject the `SimpleInteractionHandler` / `ComplexInteractionHandler` somewhere and add the listeners / bulkOverWrite your commands
as previously shown.

````java

@Component
public class MyBeans {
    @Bean
    public SimpleInteractionHandler getInteractionHandler(List<Interaction> interactions) {
        SimpleInteractionHandler simpleInteractionHandler = new SimpleInteractionHandler();
        interactions.forEach(simpleInteractionHandler::registerInteraction);
        return simpleInteractionHandler;
    }
}
````

## 📦 Installation

Replace {VERSION} with the one shown above from Maven Central

Maven

```xml

<dependency>
    <groupId>net.fellbaum</groupId>
    <artifactId>dih</artifactId>
    <version>{VERSION}</version>
</dependency>
```

Gradle

```groovy
dependencies {
    implementation 'net.fellbaum:dih:{VERSION}'
}
```

Gradle Kotlin DSL

```kotlin
dependencies {
    implementation("net.fellbaum:dih:{VERSION}")
}
```

