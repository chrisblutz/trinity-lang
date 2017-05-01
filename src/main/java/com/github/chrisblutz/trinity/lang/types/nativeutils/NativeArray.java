package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
class NativeArray {
    
    static void register() {
        
        TrinityNatives.registerMethod("Array", "toString", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            StringBuilder str = new StringBuilder("[");
            
            List<TYObject> objects = TrinityNatives.cast(TYArray.class, thisObj, stackTrace).getInternalList();
            
            for (int i = 0; i < objects.size(); i++) {
                
                str.append(TrinityNatives.cast(TYString.class, objects.get(i).tyInvoke("toString", runtime, stackTrace, null, null), stackTrace).getInternalString());
                
                if (i < objects.size() - 1) {
                    
                    str.append(", ");
                }
            }
            
            str.append("]");
            
            return new TYString(str.toString());
        });
        TrinityNatives.registerMethod("Array", "length", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getArrayLength(TrinityNatives.cast(TYArray.class, thisObj, stackTrace)));
        TrinityNatives.registerMethod("Array", "add", false, new String[]{"value"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj, stackTrace);
            
            NativeStorage.clearArrayData(thisArray);
            
            return new TYBoolean(thisArray.getInternalList().add(runtime.getVariable("value")));
        });
        TrinityNatives.registerMethod("Array", "insert", false, new String[]{"index", "value"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj, stackTrace);
            
            NativeStorage.clearArrayData(thisArray);
            
            thisArray.getInternalList().add(TrinityNatives.cast(TYInt.class, runtime.getVariable("index"), stackTrace).getInternalInteger(), runtime.getVariable("value"));
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Array", "remove", false, new String[]{"index"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj, stackTrace);
            
            NativeStorage.clearArrayData(thisArray);
            
            return thisArray.getInternalList().remove(TrinityNatives.cast(TYInt.class, runtime.getVariable("index"), stackTrace).getInternalInteger());
        });
        TrinityNatives.registerMethod("Array", "removeObject", false, new String[]{"value"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj, stackTrace);
            
            NativeStorage.clearArrayData(thisArray);
            
            return new TYBoolean(thisArray.getInternalList().remove(runtime.getVariable("value")));
        });
        TrinityNatives.registerMethod("Array", "+", false, new String[]{"other"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj, stackTrace);
            List<TYObject> objects = new ArrayList<>();
            objects.addAll(thisArray.getInternalList());
            
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYArray) {
                
                objects.addAll(((TYArray) obj).getInternalList());
                
            } else {
                
                objects.add(obj);
            }
            
            return new TYArray(objects);
        });
        TrinityNatives.registerMethod("Array", "[]", false, new String[]{"index"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject obj = runtime.getVariable("index");
            
            return TrinityNatives.cast(TYArray.class, thisObj, stackTrace).getInternalList().get(TrinityNatives.cast(TYInt.class, obj, stackTrace).getInternalInteger());
        });
    }
}
