package net.refractionapi.refraction.feature.atda;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface DataHolder {
    @Nonnull
    <O, D extends AtdaData<D>> Optional<D> getAtda(Atda<O, D> holder);

    default <C, D extends IAtdaProvider> void addData(Atda<C, ?> registry, D providers) {

    }
}
