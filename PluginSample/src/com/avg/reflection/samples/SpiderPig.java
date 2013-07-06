package com.avg.reflection.samples;

import com.avg.reflection.pluggable.ISuperHero;

public class SpiderPig implements ISuperHero {

	@Override
	public String doesWhateverSuperHeroDoes() {
		return "oink oink";
	}

	@Override
	public int getSuperHeroID() {
		return 1;
	}

}
