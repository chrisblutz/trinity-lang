package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class BinaryEqualityOperationInstructionSet extends ObjectEvaluator {
    
    private Token operator;
    private ChainedInstructionSet operand;
    
    public BinaryEqualityOperationInstructionSet(Token operator, ChainedInstructionSet operand, String fileName, File fullFile, int lineNumber) {
        
        super(fileName, fullFile, lineNumber);
        
        this.operator = operator;
        this.operand = operand;
    }
    
    public Token getOperator() {
        
        return operator;
    }
    
    public ChainedInstructionSet getOperand() {
        
        return operand;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime, TYStackTrace stackTrace) {
        
        TYObject opObj = getOperand().evaluate(TYObject.NONE, runtime, stackTrace);
        
        switch (getOperator()) {
            
            case EQUAL_TO:
                
                return thisObj.tyInvoke("==", runtime, stackTrace, null, null, opObj);
            
            case NOT_EQUAL_TO:
                
                return new TYBoolean(!TrinityNatives.cast(TYBoolean.class, thisObj.tyInvoke("==", runtime, stackTrace, null, null, opObj), stackTrace).getInternalBoolean());
        }
        
        return TYBoolean.FALSE;
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        String str = indent + "BinaryEqualityOperationInstructionSet [" + getOperator() + "]";
        
        str += "\n" + indent + getOperand().toString(indent + "\t");
        
        return str;
    }
}
