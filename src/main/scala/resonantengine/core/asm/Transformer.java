package resonantengine.core.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.*;
import resonantengine.lib.asm.ASMHelper;
import resonantengine.lib.asm.ObfMapping;

import java.util.Iterator;

public class Transformer implements IClassTransformer {
   public byte[] transform(String name, String transformedName, byte[] bytes) {
      if (!transformedName.equals("net.minecraft.world.chunk.Chunk")) {
         return bytes;
      } else {
         System.out.println("[Resonant-Engine] Transforming Chunk class for chunkModified event.");
         ClassNode cnode = ASMHelper.createClassNode(bytes);
         Iterator i$ = cnode.methods.iterator();

         while(true) {
            MethodNode method;
            ObfMapping m;
            do {
               if (!i$.hasNext()) {
                  return ASMHelper.createBytes(cnode, 0);
               }

               method = (MethodNode)i$.next();
               m = (new ObfMapping(cnode.name, method.name, method.desc)).toRuntime();
            } while(!m.s_name.equals("func_150807_a"));

            System.out.println("[Resonant-Engine] Found method " + m.s_name);
            InsnList list = new InsnList();
            list.add(new VarInsnNode(25, 0));
            list.add(new VarInsnNode(21, 1));
            list.add(new VarInsnNode(21, 2));
            list.add(new VarInsnNode(21, 3));
            list.add(new VarInsnNode(25, 4));
            list.add(new VarInsnNode(21, 5));
            list.add(new MethodInsnNode(184, "resonantengine/core/asm/StaticForwarder", "chunkSetBlockEvent", "(Lnet/minecraft/world/chunk/Chunk;IIILnet/minecraft/block/Block;I)V"));

            AbstractInsnNode lastInsn;
            for(lastInsn = method.instructions.getLast(); lastInsn instanceof LabelNode || lastInsn instanceof LineNumberNode; lastInsn = lastInsn.getPrevious()) {
            }

            if (this.isReturn(lastInsn)) {
               method.instructions.insertBefore(lastInsn, list);
            } else {
               method.instructions.insert(list);
            }

            System.out.println("[Resonant-Engine] Injected instruction to method: " + m.s_name);
         }
      }
   }

   private boolean isReturn(AbstractInsnNode node) {
      switch(node.getOpcode()) {
      case 169:
      case 172:
      case 173:
      case 174:
      case 175:
      case 176:
      case 177:
         return true;
      case 170:
      case 171:
      default:
         return false;
      }
   }
}
