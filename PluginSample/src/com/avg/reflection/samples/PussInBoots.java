package com.avg.reflection.samples;

import com.avg.reflection.pluggable.ISuperHero;

public class PussInBoots implements ISuperHero {

	@Override
	public String doesWhateverSuperHeroDoes() {
		return "fur balls!!!!";
	}

	@Override
	public int getSuperHeroID() {
		return 2;
	}

}
