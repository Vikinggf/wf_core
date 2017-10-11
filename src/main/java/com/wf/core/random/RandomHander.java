//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wf.core.random;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RandomHander {
    private final Random random = new Random();

    public RandomHander() {
    }

    public <T extends RandomWeightInfo> int getRandom(List<T> list) {
        int random = -1;
        double sumWeight = 0.0D;

        RandomWeightInfo p;
        for(Iterator var5 = list.iterator(); var5.hasNext(); sumWeight += p.getValue()) {
            p = (RandomWeightInfo)var5.next();
        }

        double randomNumber = this.getRandomDouble();
        double d1 = 0.0D;
        double d2 = 0.0D;

        for(int i = 0; i < list.size(); ++i) {
            d2 += ((RandomWeightInfo)list.get(i)).getValue() / sumWeight;
            if (i == 0) {
                d1 = 0.0D;
            } else {
                d1 += ((RandomWeightInfo)list.get(i - 1)).getValue() / sumWeight;
            }

            if (randomNumber >= d1 && randomNumber <= d2) {
                random = i;
                break;
            }
        }

        return random;
    }

    public <T> RandomInfo<T> createRandomInfo(RandomParams<T> params) {
        RandomInfo<T> info = new RandomInfo();
        Iterator var3 = params.list().iterator();

        while(var3.hasNext()) {
            RandomParams.RandomParam<T> param = (RandomParams.RandomParam)var3.next();
            info.add(param.data, param.count);
        }

        return info;
    }

    public <T> T getRandomValue(RandomInfo<T> info) {
        if (info != null && info.count() != 0) {
            return info.getValue(this.getRandomValue(info.count()));
        } else {
            throw new NullPointerException("info不能为空");
        }
    }

    public double getRandomDouble() {
        return this.random.nextDouble();
    }

    public int getRandomValue(int maxValue) {
        return this.random.nextInt(maxValue);
    }

    public int getRandomValue(int minValu, int maxValue) {
        return this.random.nextInt(maxValue - minValu + 1) + minValu;
    }
}
