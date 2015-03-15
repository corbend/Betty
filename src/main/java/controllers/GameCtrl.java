package main.java.controllers;

import main.java.managers.games.GameManager;
import main.java.models.games.Game;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@RequestScoped
public class GameCtrl implements Serializable {

    @EJB
    private GameManager gameManager;

    private Game game = new Game();

    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }

    public List<Game> findAllGames() {
        List<Game> gs = gameManager.findAllGames();
        return gs;
    }

    public String doCreateGame() {
        return "createGame.xhtml";
    }

    public String createGame() {
        try {
            gameManager.createNewGame(game);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, game.getName() + "created!",
                            "User" + game.getName() + " was created with id=" + game.getId()));

        } catch(Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), "problem with save game"));
        }

        game = new Game();
        return "gameList.xhtml";
    }
}
