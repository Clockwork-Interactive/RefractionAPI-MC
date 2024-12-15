package net.refractionapi.refraction.mixininterfaces;

import net.refractionapi.refraction.feature.atda.Atda;
import net.refractionapi.refraction.feature.atda.IAtdaProvider;

public interface IEntity extends IAtdaProvider {

    <C, D extends IAtdaProvider> void addData(Atda<C, ?> registry, D providers);

}
