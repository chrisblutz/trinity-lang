package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
class NativeArray {
    
    static void register() {
        
        TrinityNatives.registerMethod("Trinity.Array", "length", false, null, null, null, null, (runtime, thisObj, params) -> NativeStorage.getArrayLength(TrinityNatives.cast(TYArray.class, thisObj)));
        TrinityNatives.registerMethod("Trinity.Array", "add", false, new String[]{"value"}, null, null, null, (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj);
            
            NativeStorage.clearArrayData(thisArray);
            
            return TYBoolean.valueFor(thisArray.getInternalList().add(runtime.getVariable("value")));
        });
        TrinityNatives.registerMethod("Trinity.Array", "insert", false, new String[]{"index", "value"}, null, null, null, (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj);
            
            NativeStorage.clearArrayData(thisArray);
            
            thisArray.getInternalList().add(TrinityNatives.toInt(runtime.getVariable("index")), runtime.getVariable("value"));
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.Array", "remove", false, new String[]{"index"}, null, null, null, (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj);
            
            NativeStorage.clearArrayData(thisArray);
            
            return thisArray.getInternalList().remove(TrinityNatives.toInt(runtime.getVariable("index")));
        });
        TrinityNatives.registerMethod("Trinity.Array", "clear", false, null, null, null, null, (runtime, thisObj, params) -> {
            
            TrinityNatives.cast(TYArray.class, thisObj).getInternalList().clear();
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.Array", "[]", false, new String[]{"index"}, null, null, null, (runtime, thisObj, params) -> {
            
            int index = TrinityNatives.toInt(runtime.getVariable("index"));
            List<TYObject> thisList = TrinityNatives.cast(TYArray.class, thisObj).getInternalList();
            
            if (index >= thisList.size() || index < 0) {
                
                Errors.throwError("Trinity.Errors.IndexOutOfBoundsError", runtime, "Index: " + index + ", Size: " + thisList.size());
            }
            
            return thisList.get(index);
        });
        TrinityNatives.registerMethod("Trinity.Array", "copyOf", true, new String[]{"array"}, null, null, null, new ProcedureAction() {
            
            @Override
            public TYObject onAction(TYRuntime runtime, TYObject thisObj, TYObject... params) {
                
                TYArray thisArray = TrinityNatives.cast(TYArray.class, runtime.getVariable("array"));
                return new TYArray(new ArrayList<>(thisArray.getInternalList()));
            }
        });
    }
}
