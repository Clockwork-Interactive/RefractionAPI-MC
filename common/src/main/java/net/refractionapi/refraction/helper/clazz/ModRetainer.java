package net.refractionapi.refraction.helper.clazz;

import io.github.classgraph.ScanResult;
import net.refractionapi.refraction.util.Mutable;

public record ModRetainer(String modID, String pckgName, Mutable<ScanResult> result) {
}
