package net.refractionapi.refraction.mixininterfaces;

import net.refractionapi.refraction.feature.atda.Atda;
import net.refractionapi.refraction.feature.atda.IAtdaProvider;
import net.refractionapi.refraction.feature.task.Tasks;

public interface ILevel extends IAtdaProvider {
    <C, D extends IAtdaProvider> void addData(Atda<C, ?> registry, D providers);

    Tasks tasks();
}
