package org.sunflow.core.shader;

import org.sunflow.SunflowAPI;
import org.sunflow.core.ParameterList;
import org.sunflow.core.Shader;
import org.sunflow.core.ShadingState;
import org.sunflow.image.Color;

public class ViewGlobalPhotonsShader implements Shader {
    @Override
    public boolean update(ParameterList pl, SunflowAPI api) {
        return true;
    }

    @Override
    public Color getRadiance(ShadingState state) {
        state.faceforward();
        return state.getGlobalRadiance();
    }

    @Override
    public void scatterPhoton(ShadingState state, Color power) {
    }
}