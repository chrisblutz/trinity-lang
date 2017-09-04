package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.variables.Variables;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.variables.VariableLoc;


/**
 * @author Christopher Lutz
 */
public class GlobalVariableLocRetriever implements VariableLocRetriever {
    
    private String name;
    
    public GlobalVariableLocRetriever(String name) {
        
        this.name = name;
    }
    
    public String getName() {
        
        return name;
    }
    
    @Override
    public VariableLoc evaluate(TYObject thisObj, TYRuntime runtime) {
        
        if (Variables.hasGlobalVariable(getName())) {
            
            return Variables.getGlobalVariable(getName());
            
        } else {
            
            Errors.throwError("Trinity.Errors.FieldNotFoundError", runtime, "Global field '" + getName() + "' not found.");
        }
        
        return null;
    }
}
