package com.github.chrisblutz.trinity.interpreter.defaults;

import com.github.chrisblutz.trinity.interpreter.DeclarationInterpreter;
import com.github.chrisblutz.trinity.interpreter.ExpressionInterpreter;
import com.github.chrisblutz.trinity.interpreter.InterpretEnvironment;
import com.github.chrisblutz.trinity.interpreter.TrinityInterpreter;
import com.github.chrisblutz.trinity.interpreter.instructionsets.ChainedInstructionSet;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYScopeError;
import com.github.chrisblutz.trinity.lang.types.nativeutils.NativeHelper;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.blocks.BlockItem;
import com.github.chrisblutz.trinity.parser.blocks.BlockLine;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class MethodInterpreter extends DeclarationInterpreter {
    
    @Override
    public void interpret(Block block, InterpretEnvironment env) {
        
        for (int i = 0; i < block.size(); i++) {
            
            BlockItem line = block.get(i);
            
            if (line instanceof BlockLine && ((BlockLine) line).getLine().size() >= 2) {
                
                Line l = ((BlockLine) line).getLine();
                
                if (l.get(0).getToken() == Token.DEF) {
                    
                    boolean staticMethod = false, nativeMethod = false;
                    String name;
                    int position = 1;
                    
                    TokenInfo staticModifier = l.get(position);
                    if (staticModifier.getToken() == Token.STATIC) {
                        
                        staticMethod = true;
                        position++;
                    }
                    
                    if (l.get(position).getToken() == Token.NATIVE) {
                        
                        nativeMethod = true;
                        position++;
                    }
                    
                    TokenInfo nameInfo = l.get(position);
                    if (nameInfo.getToken() == Token.NON_TOKEN_STRING) {
                        
                        name = nameInfo.getContents();
                        position++;
                        
                    } else {
                        
                        name = nameInfo.getToken().getReadable();
                        position++;
                    }
                    
                    TYMethod method;
                    
                    if (!nativeMethod) {
                        
                        List<String> mandatoryParams = new ArrayList<>();
                        Map<String, TYObject> optParams = new HashMap<>();
                        
                        if (position < l.size() && l.get(position).getToken() == Token.LEFT_PARENTHESIS && l.get(l.size() - 1).getToken() == Token.RIGHT_PARENTHESIS) {
                            
                            position++;
                            List<List<TokenInfo>> infoSets = new ArrayList<>();
                            List<TokenInfo> paramInfo = new ArrayList<>();
                            int level = 0;
                            for (int pos = position; pos < l.size() - 1; pos++) {
                                
                                TokenInfo info = l.get(pos);
                                
                                if (level == 0 && info.getToken() == Token.COMMA) {
                                    
                                    List<TokenInfo> newList = new ArrayList<>();
                                    newList.addAll(paramInfo);
                                    infoSets.add(newList);
                                    paramInfo.clear();
                                    
                                } else {
                                    
                                    if (info.getToken() == Token.LEFT_PARENTHESIS) {
                                        
                                        level++;
                                        
                                    } else if (info.getToken() == Token.RIGHT_PARENTHESIS) {
                                        
                                        level--;
                                    }
                                    
                                    paramInfo.add(info);
                                }
                            }
                            
                            if (!paramInfo.isEmpty()) {
                                
                                infoSets.add(paramInfo);
                            }
                            
                            for (List<TokenInfo> list : infoSets) {
                                
                                if (list.size() == 1 && list.get(0).getToken() == Token.NON_TOKEN_STRING) {
                                    
                                    mandatoryParams.add(list.get(0).getContents());
                                    
                                } else if (list.size() > 2 && list.get(0).getToken() == Token.NON_TOKEN_STRING && list.get(1).getToken() == Token.ASSIGNMENT_OPERATOR) {
                                    
                                    List<TokenInfo> newList = new ArrayList<>();
                                    newList.addAll(list);
                                    newList.remove(0);
                                    newList.remove(0);
                                    
                                    ChainedInstructionSet value = ExpressionInterpreter.interpret(env.getLastClass().getName(), name, block.getFileName(), block.getFullFile(), l.getLineNumber(), newList.toArray(new TokenInfo[newList.size()]), null);
                                    TYObject valueResult = TYObject.NIL;
                                    
                                    if (value != null) {
                                        
                                        valueResult = value.evaluate(TYObject.NONE, new TYRuntime(), new TYStackTrace());
                                    }
                                    
                                    optParams.put(list.get(0).getContents(), valueResult);
                                }
                            }
                        }
                        
                        ProcedureAction action;
                        if (i + 1 < block.size() && block.get(i + 1) instanceof Block) {
                            
                            Block body = (Block) block.get(i + 1);
                            action = ExpressionInterpreter.interpret(body, env, env.getClassStack().get(env.getClassStack().size() - 1).getName(), name, true);
                            
                            i++;
                            
                        } else {
                            
                            action = (runtime, stackTrace, thisObj, params) -> TYObject.NONE;
                        }
                        
                        method = new TYMethod(name, staticMethod, null, new TYProcedure(action));
                        method.setMandatoryParameters(mandatoryParams);
                        method.setOptionalParameters(optParams);
                        method.importModules(TrinityInterpreter.getImportedModules());
                        
                    } else {
                        
                        method = NativeHelper.getNativeMethod(env.getEnvironmentString() + "." + name);
                    }
                    
                    if (!env.getClassStack().isEmpty()) {
                        
                        env.getClassStack().get(env.getClassStack().size() - 1).registerMethod(method);
                        
                    } else {
                        
                        TYError error = new TYError(new TYScopeError(), "Methods must be declared within a class.", new TYStackTrace());
                        error.throwError();
                    }
                }
            }
        }
    }
}
