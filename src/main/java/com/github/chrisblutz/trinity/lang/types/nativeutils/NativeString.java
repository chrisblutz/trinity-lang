package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYFloat;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Christopher Lutz
 */
class NativeString {
    
    protected static void register() {
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.STRING, "chars", (runtime, thisObj, params) -> TrinityNatives.cast(TYString.class, thisObj).getCharacterArray());
        TrinityNatives.registerMethod(TrinityNatives.Classes.STRING, "+", (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.cast(TYString.class, thisObj).getInternalString();
            
            TYObject object = runtime.getVariable("other");
            String objStr = TrinityNatives.toString(object, runtime);
            
            return new TYString(thisString + objStr);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.STRING, "==", (runtime, thisObj, params) -> {
            
            TYObject object = runtime.getVariable("other");
            
            if (!(object instanceof TYString)) {
                
                return TYBoolean.FALSE;
            }
            
            return TYBoolean.valueFor(TrinityNatives.cast(TYString.class, thisObj).getInternalString().contentEquals(TrinityNatives.cast(TYString.class, object).getInternalString()));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.STRING, "match", (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.cast(TYString.class, thisObj).getInternalString();
            String regex = TrinityNatives.cast(TYString.class, runtime.getVariable("regex")).getInternalString();
            String options = TrinityNatives.cast(TYString.class, runtime.getVariable("options")).getInternalString();
            
            int flags = 0;
            if (options.contains("i")) {
                
                flags |= Pattern.CASE_INSENSITIVE;
            }
            if (options.contains("m")) {
                
                flags |= Pattern.MULTILINE;
            }
            if (options.contains("x")) {
                
                flags |= Pattern.COMMENTS;
            }
            if (options.contains("d")) {
                
                flags |= Pattern.DOTALL;
            }
            
            Pattern pattern = Pattern.compile(regex, flags);
            Matcher matcher = pattern.matcher(thisString);
            
            TYObject bool = TrinityNatives.getObjectFor(matcher.matches());
            TYArray array;
            if (matcher.matches()) {
                
                String[] groups = new String[matcher.groupCount() + 1];
                for (int i = 0; i < matcher.groupCount() + 1; i++) {
                    
                    groups[i] = matcher.group(i);
                }
                array = TrinityNatives.getArrayFor(groups);
                
            } else {
                
                array = new TYArray(new ArrayList<>());
            }
            
            return TrinityNatives.newInstance("Trinity.StringUtils.Regex", runtime, bool, array);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.STRING, "matches", (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.cast(TYString.class, thisObj).getInternalString();
            String regex = TrinityNatives.cast(TYString.class, runtime.getVariable("regex")).getInternalString();
            String options = TrinityNatives.cast(TYString.class, runtime.getVariable("options")).getInternalString();
            
            int flags = 0;
            if (options.contains("i")) {
                
                flags |= Pattern.CASE_INSENSITIVE;
            }
            if (options.contains("m")) {
                
                flags |= Pattern.MULTILINE;
            }
            if (options.contains("x")) {
                
                flags |= Pattern.COMMENTS;
            }
            if (options.contains("d")) {
                
                flags |= Pattern.DOTALL;
            }
            
            Pattern pattern = Pattern.compile(regex, flags);
            Matcher matcher = pattern.matcher(thisString);
            
            return TrinityNatives.getObjectFor(matcher.matches());
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.STRING, "toUpperCase", (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.toString(thisObj, runtime);
            return new TYString(thisString.toUpperCase());
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.STRING, "toLowerCase", (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.toString(thisObj, runtime);
            return new TYString(thisString.toLowerCase());
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.STRING, "startsWith", (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.toString(thisObj, runtime);
            String prefix = TrinityNatives.toString(runtime.getVariable("prefix"), runtime);
            return TYBoolean.valueFor(thisString.startsWith(prefix));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.STRING, "endsWith", (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.toString(thisObj, runtime);
            String suffix = TrinityNatives.toString(runtime.getVariable("suffix"), runtime);
            return TYBoolean.valueFor(thisString.endsWith(suffix));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.STRING, "contains", (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.toString(thisObj, runtime);
            String str = TrinityNatives.toString(runtime.getVariable("str"), runtime);
            return TYBoolean.valueFor(thisString.contains(str));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.STRING, "toInt", (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.toString(thisObj, runtime);
            int radix = TrinityNatives.toInt(runtime.getVariable("radix"));
            
            try {
                
                return TrinityNatives.getObjectFor(Integer.parseInt(thisString, radix));
                
            } catch (NumberFormatException e) {
                
                Errors.throwError(Errors.Classes.NUMBER_FORMAT_ERROR, runtime, "Input: '" + thisString + "', Radix: " + radix + ", Expected Type: Trinity.Int");
                return TrinityNatives.getObjectFor(0);
            }
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.STRING, "toLong", (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.toString(thisObj, runtime);
            int radix = TrinityNatives.toInt(runtime.getVariable("radix"));
            
            try {
                
                return TrinityNatives.getObjectFor(Long.parseLong(thisString, radix));
                
            } catch (NumberFormatException e) {
                
                Errors.throwError(Errors.Classes.NUMBER_FORMAT_ERROR, runtime, "Input: '" + thisString + "', Radix: " + radix + ", Expected Type: Trinity.Long");
                return TrinityNatives.getObjectFor(0L);
            }
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.STRING, "toFloat", (runtime, thisObj, params) -> new TYFloat(TrinityNatives.toFloat(thisObj)));
        TrinityNatives.registerMethod(TrinityNatives.Classes.STRING, "compareTo", (runtime, thisObj, params) -> {
            
            String first = TrinityNatives.toString(thisObj, runtime);
            String second = TrinityNatives.toString(runtime.getVariable("other"), runtime);
            
            return new TYInt(first.compareTo(second));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.STRING, "compareToIgnoreCase", (runtime, thisObj, params) -> {
            
            String first = TrinityNatives.toString(thisObj, runtime);
            String second = TrinityNatives.toString(runtime.getVariable("other"), runtime);
            
            return new TYInt(first.compareToIgnoreCase(second));
        });
    }
}
