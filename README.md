# Discord Interaction Handler
![Maven Central](https://img.shields.io/maven-central/v/net.fellbaum/dih?label=Discord%20Interaction%20handler)

![Supported Java Version](https://img.shields.io/badge/Supported%20Java%20Version-17%2B-brightgreen)

Discord Interaction Handler is a library for handling any kind of interaction that you can receive from Discord. 
The issue is that Discord has a lot of different interaction types, and it's hard to handle them all. This library will 
help you with the management of slash commands and responding to any interaction in an easy way. 

## Supported Discord libraries
- [Javacord](https://github.com/Javacord/Javacord)

## Important information
- Currently, you have to overwrite server application commands on each startup for each server, otherwise the application commands on this server will be bulk overwritten with no application commands so the server has no longer access to them.

## Examples
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
            case BAN -> ban(interaction);
            default -> System.out.println("Unknown subcommand");
        }
    }
    
    private void ban(SlashCommandInteraction interaction){
        User user = interaction.getArguments().get(0).getUserValue().get();
        Server server = interaction.getServer().get();
        
        server.banUser(user).exceptionally(ExceptionLogger.get());
    }
}
````
#### AutoCompleteInteraction
If you want to receive `AutoCompleteInteraction`s, you have to override one of the 25 `autocompletionHandler_X` when extending
a `GlobalSlashCommand / ServerSlashCommand`. The correct one depends on the exact position of the auto completable option when you define the command.
If you added another argument to the command above after the user, you would have to override `autocompletionHandler_1` as its position is 1 when counting from 0.


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
The custom ID will internally be converted to a **RegEx**. In cases where the ID transmits some data like in the example shown above, hard coded String do not work.
To overcome this issue the receiving component ID from the interaction will be matched against the Custom ID RegEx.
If multiple component are matched when you receive a button interaction it will show you a warning and no component will be executed.


## The InteractionHandler
The interaction handler is where everything comes together.
After you have created your files of components and commands, first you have to add them to the handler.

Then you just need to add the listeners and execute `InteractionHandler#bulkOverwriteGlobalApplicationCommands` to register your command at Discord.

````java
public class Main {
    public static void main(String[] args) {
        DiscordApi api = new DiscordApiBuilder()
                .setToken("TOKEN")
                .setAllIntents()
                .login().join();

        InteractionHandler interactionHandler = new InteractionHandler();
        
        interactionHandler.registerInteraction(new ModerationCommands());
        interactionHandler.registerInteraction(new NextPageButton());
        
        interactionHandler.attachListeners(api);
        //Exchange the commands on Discord with your currently to the interaction handler added ones
        interactionHandler.bulkOverwriteGlobalApplicationCommands(api);
    }    
}
````
After that you are ready receive and handle the interactions based on what you have registered on the `InteractionHandler`.

### Context menu commands
It is pretty straight forward to use context menu commands. Simply extend ``MessageContextMenuCommand`` or ``UserContextMenuCommand`` to register and listen for a context menu command.

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
With a new Bean you can easily gather the instances of all your components and commands and register them to the `InteractionHandler`.
After that you just need to inject the InteractionHandler somewhere and add the listeners / bulkOverWrite your commands as previously shown.
````java
@Bean
public InteractionHandler getInteractionHandler(List<Interaction> interactions) {
    InteractionHandler interactionHandler = new InteractionHandler();
    interactions.forEach(interactionHandler::registerInteraction);
    return interactionHandler;
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

