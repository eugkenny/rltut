package rltut.screens;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import asciiPanel.AsciiPanel;
import rltut.*;

public class PlayScreen implements Screen {

    private World world;
    private Creature player;
    private List<String> messages;
    private int screenWidth;
    private int screenHeight;
    private FieldOfView fov;
    private Screen subscreen;

    public PlayScreen(){
        screenWidth = 80;
        screenHeight = 21;
        messages = new ArrayList<String>();
        createWorld();

        fov = new FieldOfView(world);

        StuffFactory factory = new StuffFactory(world);
        createCreatures(factory);
        createItems(factory);
    }

    private void createWorld(){
        world = new WorldBuilder(90, 32, 5)
                .makeCaves()
                .build();
    }

    private void createCreatures(StuffFactory factory){
        player = factory.newPlayer(messages, fov);

        for (int z = 0; z < world.depth(); z++){
            for (int i = 0; i < 8; i++){
                factory.newFungus(z);
            }
            for (int i = 0; i < 20; i++){
                factory.newBat(z);
            }
            for (int i = 0; i < z + 3; i++){
                factory.newZombie(z, player);
            }
        }
    }

    private void createItems(StuffFactory factory) {
        for (int z = 0; z < world.depth(); z++){
            for (int i = 0; i < world.width() * world.height() / 20; i++){
                factory.newRock(z);
            }
            factory.newFruit(z);
            factory.newEdibleWeapon(z);
            factory.newBread(z);
            factory.randomArmor(z);
            factory.randomWeapon(z);
            factory.randomWeapon(z);
        }
        factory.newVictoryItem(world.depth() - 1);
    }

    public int getScrollX() { return Math.max(0, Math.min(player.x - screenWidth / 2, world.width() - screenWidth)); }

    public int getScrollY() { return Math.max(0, Math.min(player.y - screenHeight / 2, world.height() - screenHeight)); }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        fov.update(player.x, player.y, player.z, player.visionRadius());

        for (int x = 0; x < screenWidth; x++){
            for (int y = 0; y < screenHeight; y++){
                int wx = x + left;
                int wy = y + top;

                if (player.canSee(wx, wy, player.z))
                    terminal.write(world.glyph(wx, wy, player.z), x, y, world.color(wx, wy, player.z));
                else
                    terminal.write(fov.tile(wx, wy, player.z).glyph(), x, y, Color.darkGray);
            }
        }
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {

        int left = getScrollX();
        int top = getScrollY();

        displayTiles(terminal, left, top);
        displayMessages(terminal, messages);

        //terminal.writeCenter("-- press [escape] to lose or [enter] to win --", 22);

        String stats = String.format(" %3d/%3d hp %8s", player.hp(), player.maxHp(), hunger());
        terminal.write(stats, 1, 23);

        if (subscreen != null)
            subscreen.displayOutput(terminal);
    }

    private void displayMessages(AsciiPanel terminal, List<String> messages) {
        int top = screenHeight - messages.size();
        for (int i = 0; i < messages.size(); i++){
            terminal.writeCenter(messages.get(i), top + i);
        }
        messages.clear();
    }


    @Override
    public Screen respondToUserInput(KeyEvent key) {
        int level = player.level();
        if (subscreen != null) {
            subscreen = subscreen.respondToUserInput(key);
        } else {
            switch (key.getKeyCode()){
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_H: player.moveBy(-1, 0, 0); break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_L: player.moveBy( 1, 0, 0); break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_K: player.moveBy( 0,-1, 0); break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_J: player.moveBy( 0, 1, 0); break;
                case KeyEvent.VK_Y: player.moveBy(-1,-1, 0); break;
                case KeyEvent.VK_U: player.moveBy( 1,-1, 0); break;
                case KeyEvent.VK_B: player.moveBy(-1, 1, 0); break;
                case KeyEvent.VK_N: player.moveBy( 1, 1, 0); break;
                case KeyEvent.VK_D: subscreen = new DropScreen(player); break;
                case KeyEvent.VK_E: subscreen = new EatScreen(player); break;
                case KeyEvent.VK_W: subscreen = new EquipScreen(player); break;
                case KeyEvent.VK_X: subscreen = new ExamineScreen(player); break;
                case KeyEvent.VK_SEMICOLON: subscreen = new LookScreen(player, "Looking",
                        player.x - getScrollX(),
                        player.y - getScrollY()); break;
            }

            switch (key.getKeyChar()){
                case 'g':
                case ',': player.pickup(); break;
                case '<':
                    if (userIsTryingToExit())
                        return userExits();
                    else
                        player.moveBy( 0, 0, -1);
                    break;
                case '>': player.moveBy( 0, 0, 1); break;
                case '?': subscreen = new HelpScreen(); break;
            }
        }

        if (player.level() > level)
            subscreen = new LevelUpScreen(player, player.level() - level);

        if (subscreen == null)
            world.update();

        if (player.hp() < 1)
            return new LoseScreen();

        return this;
    }

    private boolean userIsTryingToExit(){
        return player.z == 0 && world.tile(player.x, player.y, player.z) == Tile.STAIRS_UP;
    }


    private Screen userExits(){
        for (Item item : player.inventory().getItems()){
            if (item != null && item.name().equals("teddy bear"))
                return new WinScreen();
        }
        return new LoseScreen();
    }

    private String hunger(){
        if (player.food() < player.maxFood() * 0.1)
            return "Starving";
        else if (player.food() < player.maxFood() * 0.2)
            return "Hungry";
        else if (player.food() > player.maxFood() * 0.9)
            return "Stuffed";
        else if (player.food() > player.maxFood() * 0.8)
            return "Full";
        else
            return "";
    }
}