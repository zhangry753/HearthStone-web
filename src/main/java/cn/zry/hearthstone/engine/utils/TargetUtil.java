package cn.zry.hearthstone.engine.utils;

import cn.zry.hearthstone.engine.constant.Target;

public class TargetUtil {
	
	public static int transToSelfServant(Target target){
		if(target==Target.SELF_FIELD_1 || target==Target.SELF_FIELD_2 || target==Target.SELF_FIELD_3 || target==Target.SELF_FIELD_4 
				  || target==Target.SELF_FIELD_5 || target==Target.SELF_FIELD_6 || target==Target.SELF_FIELD_7)
			return target.ordinal()-2;
		else 
			return -1;
	}
	
	public static int transToOppoServant(Target target){
		if(target==Target.OPPO_FIELD_1 || target==Target.OPPO_FIELD_2 || target==Target.OPPO_FIELD_3 || target==Target.OPPO_FIELD_4
				 || target==Target.OPPO_FIELD_5 || target==Target.OPPO_FIELD_6 || target==Target.OPPO_FIELD_7)
			return target.ordinal()-10;
		else 
			return -1;
	}
	
	public static int transToField(Target target){
		if(target==Target.SELF_FIELD_1 || target==Target.SELF_FIELD_2 || target==Target.SELF_FIELD_3 || target==Target.SELF_FIELD_4 
				  || target==Target.SELF_FIELD_5 || target==Target.SELF_FIELD_6 || target==Target.SELF_FIELD_7 || target==Target.SELF_FIELD_RIGHT)
			return target.ordinal()-1;
		else 
			return -1;
	}
}
