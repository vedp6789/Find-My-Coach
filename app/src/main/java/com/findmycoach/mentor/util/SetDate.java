package com.findmycoach.mentor.util;

import android.media.JetPlayer;

/**
 * Created by praka_000 on 2/17/2015.
 */
public interface SetDate {
    public void setSelectedStartDate(Object o1,Object o2,Object o3);
    public void setSelectedTillDate(Object o1,Object o2,Object o3,boolean b);
    public void setStartInitialLimit(Object o1,Object o2, Object o3);
    public void setStartUpperLimit(Object o1,Object o2, Object o3);
    public int[] getTillInitialLimit();
    public void setTillUpperLimit(Object o1,Object o2, Object o3);

}
