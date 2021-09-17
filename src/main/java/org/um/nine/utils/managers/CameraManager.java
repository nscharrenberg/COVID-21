package org.um.nine.utils.managers;

import com.google.inject.Inject;
import com.jme3.math.Vector3f;
import org.um.nine.contracts.repositories.IGameRepository;

public class CameraManager {
    @Inject
    private IGameRepository gameRepository;

    public void localTranslateX(float value) {
        Vector3f m = gameRepository.getMap().getLocalTranslation();
        gameRepository.getMap().setLocalTranslation(m.x + value * gameRepository.getSpeed(), m.y, m.z);
    }

    public void localTranslateY(float value) {
        Vector3f m = gameRepository.getMap().getLocalTranslation();
        gameRepository.getMap().setLocalTranslation(m.x, m.y + value * gameRepository.getSpeed(), m.z);
    }

    public void localTranslateZ(float value) {
        Vector3f m = gameRepository.getMap().getLocalTranslation();
        gameRepository.getMap().setLocalTranslation(m.x, m.y, m.z + value * gameRepository.getSpeed());
    }

    public void localTranslation(float x, float y, float z) {
        Vector3f m = gameRepository.getMap().getLocalTranslation();
        gameRepository.getMap().setLocalTranslation(m.x + x * gameRepository.getSpeed(), m.y + y * gameRepository.getSpeed(), m.z + z * gameRepository.getSpeed());
    }
}
