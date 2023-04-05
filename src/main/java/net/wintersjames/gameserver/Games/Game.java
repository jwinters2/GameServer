package net.wintersjames.gameserver.Games;

import java.io.Serializable;

/**
 *
 * @author james
 */
public class Game implements Serializable {
    public String title;
    public String image;
    public String description;
    public boolean isSinglePlayer;
    public boolean isMultiPlayer;
    
    protected Game() {};
    
    public Game(String title, String image, String description)  {
        this.title = title;
        this.image = image;
        this.description = description;
        this.isSinglePlayer = false;
        this.isMultiPlayer = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIsSinglePlayer() {
        return isSinglePlayer;
    }

    public void setIsSinglePlayer(boolean isSinglePlayer) {
        this.isSinglePlayer = isSinglePlayer;
    }

    public boolean isIsMultiPlayer() {
        return isMultiPlayer;
    }

    public void setIsMultiPlayer(boolean isMultiPlayer) {
        this.isMultiPlayer = isMultiPlayer;
    }
    
    
}
