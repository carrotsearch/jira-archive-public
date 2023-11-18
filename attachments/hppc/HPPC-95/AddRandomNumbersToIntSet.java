package data;

import java.util.concurrent.ThreadLocalRandom;

import com.carrotsearch.hppc.IntDoubleLinkedSet;
import com.carrotsearch.hppc.IntSet;

public class AddRandomNumbersToIntSet {
	public static void main(final String[] args) {
		final ThreadLocalRandom r = ThreadLocalRandom.current();
		final IntSet set = new IntDoubleLinkedSet();
		for(int i = 0; i < 10_000; i++) {
			set.add(r.nextInt());
		}
	}
}
