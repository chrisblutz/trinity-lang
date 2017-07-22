package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.variables.VariableManager;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class KeyRetrievalInstructionSet extends InstructionSet {
    
    public KeyRetrievalInstructionSet(TokenInfo[] tokens, String fileName, File fullFile, int lineNumber) {
        
        super(tokens, fileName, fullFile, lineNumber);
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        if (getTokens().length >= 1) {
            
            if (getTokens()[0].getToken() == Token.NON_TOKEN_STRING) {
                
                String tokenContents = getTokens()[0].getContents();
                TYObject keyObject = null;
                
                if (thisObj == TYObject.NONE) {
                    
                    if (runtime.hasVariable(tokenContents)) {
                        
                        keyObject = runtime.getVariable(tokenContents);
                        
                    } else if (runtime.getThis() != TYObject.NONE && runtime.getThis().getObjectClass().hasVariable(tokenContents, runtime.getThis())) {
                        
                        keyObject = VariableManager.getVariable(runtime.getThis().getObjectClass().getVariable(tokenContents, runtime.getThis()));
                        
                    } else if (runtime.isStaticScope() && runtime.getScopeClass().hasVariable(tokenContents)) {
                        
                        keyObject = VariableManager.getVariable(runtime.getScopeClass().getVariable(tokenContents));
                        
                    } else if (tokenContents.contentEquals("this")) {
                        
                        keyObject = runtime.getThis();
                    }
                }
                
                if (keyObject != null) {
                    
                    List<TYObject> params = new ArrayList<>();
                    
                    for (ChainedInstructionSet set : getChildren()) {
                        
                        TYObject obj = set.evaluate(TYObject.NONE, runtime);
                        
                        if (obj != TYObject.NONE) {
                            
                            params.add(obj);
                        }
                    }
                    
                    return keyObject.tyInvoke("[]", runtime, null, null, params.toArray(new TYObject[params.size()]));
                }
            }
            
        } else if (getTokens().length == 0 && thisObj != TYObject.NONE) {
            
            List<TYObject> params = new ArrayList<>();
            
            for (ChainedInstructionSet set : getChildren()) {
                
                TYObject obj = set.evaluate(TYObject.NONE, runtime);
                
                if (obj != TYObject.NONE) {
                    
                    params.add(obj);
                }
            }
            
            return thisObj.tyInvoke("[]", runtime, null, null, params.toArray(new TYObject[params.size()]));
        }
        
        return TYObject.NONE;
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        StringBuilder str = new StringBuilder(indent + "ArrayRetrievalInstructionSet [");
        
        for (TokenInfo info : getTokens()) {
            
            str.append(info.getContents());
        }
        
        str.append("]");
        
        for (ObjectEvaluator child : getChildren()) {
            
            str.append("\n").append(indent).append(child.toString(indent + "\t"));
        }
        
        return str.toString();
    }
}
