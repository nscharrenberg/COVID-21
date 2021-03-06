package org.um.nine.v1.utils.managers;

import com.google.inject.Inject;
import com.jme3.math.Vector3f;
import org.um.nine.v1.contracts.repositories.IBoardRepository;
import org.um.nine.v1.contracts.repositories.IGameRepository;

import static java.lang.Math.abs;

public class CameraManager {
    @Inject
    private IGameRepository gameRepository;

    @Inject
    private IBoardRepository boardRepository;

    public void localTranslateX(float value) {
        Vector3f initialLeftVec = gameRepository.getApp().getCamera().getLeft().clone();
        Vector3f vel = initialLeftVec.mult(value * gameRepository.getApp().getFlyByCamera().getMoveSpeed());
        Vector3f pos = gameRepository.getApp().getCamera().getLocation().clone();

        pos.addLocal(vel);

        float zoom = 70 - gameRepository.getApp().getCamera().getFov() ;
        if (abs(pos.x) > 45 * zoom) return;

        gameRepository.getApp().getCamera().setLocation(pos);
    }

    public void localTranslateY(float value) {
        Vector3f initialUpVec = gameRepository.getApp().getCamera().getUp().clone();
        Vector3f vel = initialUpVec.mult(value * gameRepository.getApp().getFlyByCamera().getMoveSpeed());
        Vector3f pos = gameRepository.getApp().getCamera().getLocation().clone();
        pos.addLocal(vel);

        float zoom = 70 - gameRepository.getApp().getCamera().getFov() ;
        if (abs(pos.y) > 21 * zoom) return;

        gameRepository.getApp().getCamera().setLocation(pos);
    }

    public void localTranslateZ(float value) {
        float newFov = gameRepository.getApp().getCamera().getFov() + value * 0.1F * gameRepository.getApp().getFlyByCamera().getZoomSpeed();
        if (abs(40 - newFov) > 20) return;
        if (newFov > 0.0F) {
            gameRepository.getApp().getCamera().setFov(newFov);
        }
    }

    public void localTranslation(float x, float y, float z) {
        Vector3f m = boardRepository.getBoard().getLocalTranslation();
        boardRepository.getBoard().setLocalTranslation(m.x + x * gameRepository.getSpeed(), m.y + y * gameRepository.getSpeed(), m.z + z * gameRepository.getSpeed());
    }
}
