package org.um.nine.utils.managers;

import com.google.inject.Inject;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.domain.*;
import org.um.nine.domain.cards.CityCard;
import org.um.nine.domain.cards.EpidemicCard;
import org.um.nine.domain.cards.EventCard;
import org.um.nine.domain.cards.InfectionCard;
import org.um.nine.exceptions.CardsLimitExceededException;

import java.util.List;


public class RenderManager {
    @Inject
    private IGameRepository gameRepository;

    @Inject
    private IBoardRepository boardRepository;

    public void renderPlayer(Player player, Vector3f offset) {
        float offsetX = offset.getX();
        float offsetY = offset.getY();

        Spatial model = gameRepository.getApp().getAssetManager().loadModel("models/pawn.j3o");
        model.setName(player.toString());
        model.rotate(45, 0, 0);
        model.setLocalTranslation(new Vector3f(player.getCity().getLocation().getX() + offsetX, player.getCity().getLocation().getY() + offsetY, player.getCity().getLocation().getZ() + 1 + offset.getZ()));
        model.setLocalScale(.75f);
        Material mat = new Material(gameRepository.getApp().getAssetManager(),
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", player.getRole().getColor() );
        mat.setColor("Ambient", player.getRole().getColor() );
        model.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(model);
    }

    public void renderResearchStation(ResearchStation researchStation, Vector3f offset) {
        float offsetX = offset.getX();
        float offsetY = offset.getY();

        Spatial model = gameRepository.getApp().getAssetManager().loadModel("models/research_station.j3o");
        model.setName(researchStation.toString());
        model.setLocalScale(1);
        model.setLocalTranslation(new Vector3f(researchStation.getCity().getLocation().getX() + offsetX, researchStation.getCity().getLocation().getY() + offsetY, researchStation.getCity().getLocation().getZ() + 5 + offset.getZ()));
        model.rotate(45, 75, 0);
        Material mat = new Material(gameRepository.getApp().getAssetManager(),
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", ColorRGBA.White ); // with Lighting.j3md
        mat.setColor("Ambient", ColorRGBA.White ); // with Lighting.j3md
        model.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(model);
    }

    public void renderCity(City city) {
        Cylinder plateShape = new Cylinder(5, 10, 12.5f, 2, true);
        Geometry plate = new Geometry(city.getName(), plateShape);
        Material mat = new Material(gameRepository.getApp().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", city.getColor() );
        mat.setColor("Ambient", city.getColor() );
        plate.setMaterial(mat);
        plate.setLocalTranslation(city.getLocation());
        gameRepository.getApp().getRootNode().attachChild(plate);
    }

    public void renderEdge(City from, City to) {
        Line lineShape = new Line(from.getLocation(), to.getLocation());
        Geometry plate = new Geometry(from.getName() + "->" + to.getName(), lineShape);
        Material mat = new Material(gameRepository.getApp().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        mat.setColor("GlowColor", ColorRGBA.White);
        mat.getAdditionalRenderState().setLineWidth(1);
        mat.getAdditionalRenderState().setWireframe(true);
        plate.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(plate);
    }

    public void renderDisease(Disease disease, Vector3f offset) {
        Spatial model = gameRepository.getApp().getAssetManager().loadModel("models/cube.j3o");
        model.setName(disease.toString());
        model.rotate(45, 0, 0);
        model.setLocalTranslation(new Vector3f(disease.getCity().getLocation().getX() + offset.getX(), disease.getCity().getLocation().getY() + offset.getY(), disease.getCity().getLocation().getZ() + 1 + offset.getZ()));
        model.setLocalScale(1.25f);
        Material mat = new Material(gameRepository.getApp().getAssetManager(),
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", disease.getColor() );
        mat.setColor("Ambient", disease.getColor() );
        model.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(model);
    }

    public void renderText(String text, Vector3f position, ColorRGBA color, String name) {
        BitmapFont myFont = gameRepository.getApp().getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        renderText(text, position, color, name, myFont.getCharSet().getRenderedSize(), myFont);
    }

    public void renderText(String text, Vector3f position, ColorRGBA color, String name, float size, BitmapFont font) {
        BitmapText found = (BitmapText) gameRepository.getApp().getRootNode().getChild(name);
        if (found != null) {
            found.setText(text);
            return;
        }

        BitmapText textBT = new BitmapText(font);
        textBT.setSize(size);
        textBT.setColor(color);
        textBT.setText(text);
        textBT.setLocalTranslation(position);
        textBT.setQueueBucket(RenderQueue.Bucket.Transparent);
        textBT.setName(name);
        gameRepository.getApp().getRootNode().attachChild(textBT);
    }

    public void renderCureMarker(Cure cure, Vector3f offset) {
        renderCureMarker(cure, offset, false);
    }

    public void renderCureMarker(Cure cure, Vector3f offset, boolean flip) {
        Spatial model = gameRepository.getApp().getAssetManager().loadModel("models/cure_marker.j3o");
        model.setName(cure.toString());

        if (flip) {
            model.rotate(45, 0, 0);
        } else {
            model.rotate(45, 135, 0);
        }

        model.setLocalTranslation(new Vector3f(0 + offset.getX(), -450 + offset.getY(), 20 + offset.getZ()));
        model.setLocalScale(3f);
        Material mat = new Material(gameRepository.getApp().getAssetManager(),
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", cure.getColor() );
        mat.setColor("Ambient", cure.getColor() );
        model.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(model);
    }

    public void renderOutbreakStar(OutbreakMarker status, Vector3f offset) {
        Material mat = new Material(gameRepository.getApp().getAssetManager(),
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", status.getColor() );
        mat.setColor("Ambient", status.getColor() );
        String name = status.toString();
        Box box = new Box(15, 15, 1);
        Geometry star = new Geometry(name, box);
        star.setLocalTranslation(-950 + offset.getX(), -225 + offset.getY(), 2 + offset.getZ());
        star.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(star);

        BitmapFont myFont = gameRepository.getApp().getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        renderText(String.valueOf(status.getId()),star.getLocalTranslation().add(-5,7.5f,1), ColorRGBA.Black,name + "-label",20,myFont);

        renderOutbreakMarker(status);
    }

    public void renderOutbreakMarker(OutbreakMarker status) {
        if (!status.isCurrent()) {
            return;
        }

        Geometry basicMarker = (Geometry) gameRepository.getApp().getRootNode().getChild(status.toString());
        String outbreakMarkerName = "outbreak-marker";
        Spatial foundOutbreakMarker = gameRepository.getApp().getRootNode().getChild(outbreakMarkerName);

        Vector3f location = basicMarker.getLocalTranslation().add(-20, 10, 0);

        if (foundOutbreakMarker != null) {
            foundOutbreakMarker.setLocalTranslation(location);
            return;
        }

        Material mat = new Material(gameRepository.getApp().getAssetManager(),
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", ColorRGBA.Green );
        mat.setColor("Ambient", ColorRGBA.Green );

        Spatial model = gameRepository.getApp().getAssetManager().loadModel("models/outbreak_marker.j3o");
        model.setName(outbreakMarkerName);
        Quaternion rotate = new Quaternion();
        rotate.fromAngleAxis( FastMath.PI / 2 , new Vector3f(1,0,0) );
        model.rotate(rotate);
        model.setLocalTranslation(location);
        model.setLocalScale(2f);
        model.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(model);
    }

    public void renderCard (Card card, int index) {
        Quad quad = new Quad(250,150);
        Geometry colored_plate = new Geometry(card.getName(),quad);
        String label = "";
        Material mat = new Material(gameRepository.getApp().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        if (card instanceof CityCard){
            mat.setColor("Color",((CityCard) card).getCity().getColor());
            label = "city card";
        } else if (card instanceof EventCard){
            mat.setColor("Color", ColorRGBA.fromRGBA255(255,171,42,100));
            label = "event card";
        } else if (card instanceof EpidemicCard){
            mat.setColor("Color", ColorRGBA.Green);
            label = "epidemic card";
        } else  if (card instanceof InfectionCard){
            mat.setColor("Color",ColorRGBA.Yellow);
            label = "infection card";
        }
        mat.getAdditionalRenderState().setLineWidth(15);
        mat.getAdditionalRenderState().setWireframe(false);
        colored_plate.setLocalTranslation(new Vector3f(-900 + (index*10) + (index*250),550,1));  //offset cards based on index
        colored_plate.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(colored_plate);

        quad = new Quad(210,130);
        Geometry white_plate = new Geometry("white plate", quad);
        Material mat2 = new Material(gameRepository.getApp().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.White);
        white_plate.setMaterial(mat2);
        white_plate.setLocalTranslation(colored_plate.getLocalTranslation().add(20,10,1));
        gameRepository.getApp().getRootNode().attachChild(white_plate);

        BitmapFont myFont = gameRepository.getApp().getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        renderText(label,colored_plate.getLocalTranslation().add(40,125,1.1f),ColorRGBA.Blue,card +"_label",10,myFont);
        renderText(card.getName(),colored_plate.getLocalTranslation().add(40,105,1.1f),ColorRGBA.Blue, card + "_description",20,myFont);
    }

    public void renderPlayerCards (List<Card> cards) {
         try {
             if (cards.size()> 7) throw new CardsLimitExceededException();
        } catch (CardsLimitExceededException e) {
            e.printStackTrace();
        }
        for (int i = 0; i< cards.size(); i++)
            renderCard(cards.get(i),i);
    }


    public void renderInfectionRateStar(InfectionRateMarker marker, Vector3f offset) {
        Cylinder plateShape = new Cylinder(5, 10, 12.5f, 2, true);
        Geometry plate = new Geometry(marker.toString(), plateShape);
        Material mat = new Material(gameRepository.getApp().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", ColorRGBA.fromRGBA255(0, 100, 0, 1));
        mat.setColor("Ambient", ColorRGBA.fromRGBA255(0, 100, 0, 1));
        plate.setMaterial(mat);
        plate.setLocalTranslation(-950 + offset.getX(), -125 + offset.getY(), 2 + offset.getZ());
        gameRepository.getApp().getRootNode().attachChild(plate);

        BitmapFont myFont = gameRepository.getApp().getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        renderText(String.valueOf(marker.getCount()),plate.getLocalTranslation().add(-5,-15,1.1f),ColorRGBA.White, marker + "-label",20,myFont);

        renderInfectionMarker(marker);
    }

    public void renderInfectionMarker(InfectionRateMarker status) {
        if (!status.isCurrent()) {
            return;
        }

        Geometry basicMarker = (Geometry) gameRepository.getApp().getRootNode().getChild(status.toString());
        String infectionRateMarker = "infection-rate-marker";
        Spatial foundInfectionRate = gameRepository.getApp().getRootNode().getChild(infectionRateMarker);

        Vector3f location = basicMarker.getLocalTranslation().add(0, 0, 0);

        if (foundInfectionRate != null) {
            foundInfectionRate.setLocalTranslation(location);
            return;
        }

        Material mat = new Material(gameRepository.getApp().getAssetManager(),
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", ColorRGBA.Green );
        mat.setColor("Ambient", ColorRGBA.Green );

        Spatial model = gameRepository.getApp().getAssetManager().loadModel("models/infection_rate_marker.j3o");
        model.setName(infectionRateMarker);
        Quaternion rotate = new Quaternion();
        rotate.fromAngleAxis( FastMath.PI / 2 , new Vector3f(1,0,0) );
        model.rotate(rotate);
        model.setLocalTranslation(location);
        model.setLocalScale(1.5f);
        model.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(model);
    }

}
