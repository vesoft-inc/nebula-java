/* Copyright (c) 2023 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * The util of reflect
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class ReflectUtil {
  
    /**
     * Break through the access rights of the object 
     *     and obtain the specified field value.
     *     
     * @param o object of field value source
     * @param field field used for get value
     * @return field value in obj
     */
    public static Object getValue(Object o, Field field) {
        try {
          boolean accessible = field.isAccessible();
          if (accessible) {
              return field.get(o);
          } else {
              field.setAccessible(true);
              Object value = field.get(o);
              field.setAccessible(false);
              return value;
          }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Determine whether parentType is paramType or its parent class or interface
     * 
     * @param paramType type to be determined - subclass
     * @param parentType type to be determined - parent class or interface
     * @return whether paramType is a subclass or implementation class of parentType
     */
    public static boolean isCurrentTypeOrParentType(Class<?> paramType, Class<?> parentType) {
        if (paramType == parentType) {
            return true;
        }
        Set<Class<?>> parentTypes = getParentTypes(paramType);
        return parentTypes.contains(parentType);
    }
  
    /**
     * Get the super class and interface type collection according to paramType.
     * 
     * @param paramType subclass type.
     * @return super class and interface.
     */
    public static Set<Class<?>> getParentTypes(Class<?> paramType) {
        if (paramType == null) {
            return Collections.EMPTY_SET;
        }
        List<Class<?>> interfaces = Arrays.asList(paramType.getInterfaces());
        Set<Class<?>> parents = new HashSet<>(interfaces);
    
        for (Class<?> anInterface : interfaces) {
            parents.addAll(getParentTypes(anInterface));
        }
    
        Class<?> superclass = paramType.getSuperclass();
        parents.add(superclass);
        parents.addAll(getParentTypes(superclass));
        return parents;
    }
}
