package com.soleus.office.domain

fun shouldRemind(nowMin: Int, start: Int, end: Int) = nowMin in start until end
