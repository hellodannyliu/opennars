/*
 * Copyright (C) 2014 me
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package nars.test.core;

import java.util.Deque;
import java.util.LinkedList;
import nars.entity.BudgetValue;
import nars.entity.Item;
import nars.storage.DefaultBag;
import nars.storage.IBag;
import nars.util.FastBag;

/**
 *
 * @author me
 */
public class BagPerf {
    
    int repeats = 8;
    int warmups = 1;
    int forgetRate = 10;
    int randomAccesses;
    double insertRatio = 0.9;
    

    
    public float totalPriority, totalMass, totalMinItemsPerLevel, totalMaxItemsPerLevel;

    public void testBag(final boolean arraylist, final int levels, final int capacity, final int forgetRate) {
        
        totalPriority = 0;
        totalMass = 0;
        totalMaxItemsPerLevel = totalMinItemsPerLevel = 0;
        
        Performance p = new Performance((arraylist ? "DequeArray" : "LinkedList")+","+levels+","+ capacity, repeats, warmups) {

            @Override public void init() { }

            @Override
            public void run(boolean warmup) {
                DefaultBag<Item> b = new DefaultBag<Item>(levels, capacity, forgetRate) {

                    @Override
                    protected Deque<Item> newLevel() {
                        if (arraylist)                                                    
                            return super.newLevel();
                        return new LinkedList<Item>();
                    }
                    
                };
                randomBagIO(b, randomAccesses, insertRatio);
                
                if (!warmup) {                    
                    totalPriority += b.getAveragePriority();
                    totalMass += b.getMass();                    
                    totalMinItemsPerLevel += b.getMinItemsPerLevel();
                    totalMaxItemsPerLevel += b.getMaxItemsPerLevel();
                }
            }
            
        }.printCSV(true);
        
        
        //items per level min
        //items per lvel max
        //avg prioirty
        //avg norm mass
        //System.out.print((totalMinItemsPerLevel/p.repeats) + ",");
        System.out.print((totalMaxItemsPerLevel/p.repeats) + ",");
        System.out.print(totalPriority/p.repeats + ",");
        System.out.print(totalMass/repeats/((float)levels) + ",");
        System.out.println();
    }
            
    public static int itemID = 0;
    
    public static class NullItem extends Item {
    
        public NullItem() {
            this((float)Math.random());
        }

        public NullItem(float priority) {
            super("" + (itemID++), new BudgetValue());
            setPriority(priority);
        }
    }
    
    public static void randomBagIO(IBag b, int accesses, double insertProportion) {
        for (int i = 0; i < accesses; i++) {
            if (Math.random() > insertProportion) {
                //remove
                b.takeOut();
            }
            else {
                //insert
                b.putIn(new NullItem());
            }            
        }
    }
    
    public static double compareBagAndFastBag(final boolean normalBag, final int levels, final int levelCapacity, final int randomAccesses, final float insertRatio, int repeats, int warmups) {
        
        final int forgetRate = 10; //changing this should not affect anything
        
        Performance p = new Performance(( (!normalBag) ? ("FastBag."+(levels*levelCapacity)) : ("Bag"+"."+levels+"x"+ levelCapacity)) , repeats, warmups) {

            @Override public void init() { }

            @Override
            public void run(boolean warmup) {
                IBag<Item> b;
            
                if (normalBag) {
                    b = new DefaultBag<Item>(levels, levels*levelCapacity, forgetRate);
                }
                else {
                    b = new FastBag<Item>(levels*levelCapacity, forgetRate);
                }
                
                randomBagIO(b, randomAccesses, insertRatio);
                
                if (!warmup) {                                        
                }
            }
            
        }.printCSV(false);
                
        System.out.println();
        return p.getCycleTimeMS();
        
    }
    
    public BagPerf() {
        
        
        for (int capacity = 8; capacity < 40000; capacity*=capacity) {
            randomAccesses = capacity*64;
            for (int i = 5; i < 200; i+=5) {
                testBag(false, i, capacity, forgetRate);
                testBag(true, i, capacity, forgetRate);
            }
        }
        
    }
    
    public static void main(String[] args) {
        //new BagPerf();
        
        
        
        
        int capacityPerLevel = 10;
        int repeats = 16;
        int warmups = 2;
        for (int i = 0; i < 2; i++)
            for (float insertRatio = 0.1f; insertRatio <= 1.0f; insertRatio += 0.1f) {
                for (int levels = 1; levels <= 150; levels += 10) {

                    int randomAccesses = 256 * levels;
                    int num = levels*capacityPerLevel;
                    
                    double a = 0, b = 0;
                    if (i==0) {
                        System.out.print(insertRatio+", "+num+", ");
                        a = compareBagAndFastBag(true, levels, capacityPerLevel, randomAccesses, insertRatio, repeats, warmups);
                    }                
                    else if (i==1) {
                        System.out.print(insertRatio+", "+num+", ");
                        b = compareBagAndFastBag(false, levels, capacityPerLevel, randomAccesses, insertRatio, repeats, warmups);
                    }
                    
                    System.out.println(a-b);
                }
            }
    }
    
}
