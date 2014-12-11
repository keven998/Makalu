package com.aizou.peachtravel.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoyong on 14/12/11.
 */
public class FavoritesBean {
    public final static int CONST_TYPE_SPOT = 1;
    public final static int CONST_TYPE_FOOD = 2;
    public final static int CONST_TYPE_SHOP = 3;
    public final static int CONST_TYPE_STAY = 4;
    public final static int CONST_TYPE_NOTE = 5;
    public final static int CONST_TYPE_CITY = 6;

    public String id;
    public String userId;
    public String itemId;
    public String type;
    public String zhName;
    public String enName;
    public String desc;
    public List<ImageBean> images;
    public long createTime;

    public int getType() {
          if ("hotel".equals(type)) {
            return CONST_TYPE_STAY;
          } else if ("restaurant".equals(type)) {
              return CONST_TYPE_FOOD;
          } else if ("shopping".equals(type)) {
              return CONST_TYPE_SHOP;
          } else if ("travelNote".equals(type)) {
              return CONST_TYPE_NOTE;
          } else if ("vs".equals(type)) {
              return CONST_TYPE_SPOT;
          } else {
              return CONST_TYPE_CITY;
          }
    }
}
