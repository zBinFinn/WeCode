package org.zbinfinn.wecode.features.debug;

import org.zbinfinn.wecode.config.Config;
import org.zbinfinn.wecode.features.Feature;

public abstract class DebugFeature extends Feature {
    @Override
    public boolean isEnabled() {
        return Config.getConfig().Debug;
    }
}
