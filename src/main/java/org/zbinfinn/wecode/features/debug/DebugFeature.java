package org.zbinfinn.wecode.features.debug;

import dev.dfonline.flint.feature.core.FeatureTrait;
import org.zbinfinn.wecode.config.Config;

public interface DebugFeature extends FeatureTrait {
    @Override
    default boolean isEnabled() {
        return Config.getConfig().Debug;
    }
}
