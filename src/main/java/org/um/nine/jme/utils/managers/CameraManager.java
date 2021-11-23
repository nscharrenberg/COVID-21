package org.um.nine.jme.utils.managers;

import com.jme3.math.Vector3f;
import org.um.nine.jme.JmeMain;

import static java.lang.Math.abs;

public class CameraManager {
    public void localTranslateX(float value) {
        Vector3f initialLeftVec = JmeMain.getGameRepository().getApp().getCamera().getLeft().clone();
        Vector3f vel = initialLeftVec.mult(value * JmeMain.getGameRepository().getApp().getFlyByCamera().getMoveSpeed());
        Vector3f pos = JmeMain.getGameRepository().getApp().getCamera().getLocation().clone();

        pos.addLocal(vel);

        float zoom = 70 - JmeMain.getGameRepository().getApp().getCamera().getFov() ;
        if (abs(pos.x) > 45 * zoom) return;

        JmeMain.getGameRepository().getApp().getCamera().setLocation(pos);
    }

    public void localTranslateY(float value) {
        Vector3f initialUpVec = JmeMain.getGameRepository().getApp().getCamera().getUp().clone();
        Vector3f vel = initialUpVec.mult(value * JmeMain.getGameRepository().getApp().getFlyByCamera().getMoveSpeed());
        Vector3f pos = JmeMain.getGameRepository().getApp().getCamera().getLocation().clone();
        pos.addLocal(vel);

        float zoom = 70 - JmeMain.getGameRepository().getApp().getCamera().getFov() ;
        if (abs(pos.y) > 21 * zoom) return;

        JmeMain.getGameRepository().getApp().getCamera().setLocation(pos);
    }

    public void localTranslateZ(float value) {
        float newFov = JmeMain.getGameRepository().getApp().getCamera().getFov() + value * 0.1F * JmeMain.getGameRepository().getApp().getFlyByCamera().getZoomSpeed();
        if (abs(40 - newFov) > 20) return;
        if (newFov > 0.0F) {
            JmeMain.getGameRepository().getApp().getCamera().setFov(newFov);
        }
    }

    public void localTranslation(float x, float y, float z) {
        Vector3f m = JmeMain.getVisualRepository().getBoard().getLocalTranslation();
        JmeMain.getVisualRepository().getBoard().setLocalTranslation(m.x + x * JmeMain.getGameRepository().getSpeed(), m.y + y * JmeMain.getGameRepository().getSpeed(), m.z + z * JmeMain.getGameRepository().getSpeed());
    }
}
