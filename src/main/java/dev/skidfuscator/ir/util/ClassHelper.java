package dev.skidfuscator.ir.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import dev.skidfuscator.ir.klass.KlassNode;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author Bibl (don't ban me pls)
 */
public class ClassHelper {
	public static ClassNode BOOLEAN;
	public static ClassNode BYTE;
	public static ClassNode SHORT;
	public static ClassNode CHAR;
	public static ClassNode INT;
	public static ClassNode LONG;
	public static ClassNode FLOAT;
	public static ClassNode DOUBLE;
	public static ClassNode VOID;

	static {
		BOOLEAN = new ClassNode();
		BOOLEAN.name = "Z";

		BYTE = new ClassNode();
		BYTE.name = "B";

		SHORT = new ClassNode();
		SHORT.name = "S";

		CHAR = new ClassNode();
		CHAR.name = "C";

		INT = new ClassNode();
		INT.name = "I";

		LONG = new ClassNode();
		LONG.name = "J";

		FLOAT = new ClassNode();
		FLOAT.name = "F";

		DOUBLE = new ClassNode();
		DOUBLE.name = "D";

		VOID = new ClassNode();
		VOID.name = "V";
	}


	public static Collection<ClassNode> parseClasses(Class<?>... a) throws IOException {
		List<ClassNode> list = new ArrayList<>();
		for(int i=0; i < a.length; i++) {
			list.add(create(a[i].getName()));
		}
		return list;
	}
	
	public static Map<String, KlassNode> convertToMap(Collection<KlassNode> classes) {
		Map<String, KlassNode> map = new HashMap<>();
		for (KlassNode cn : classes) {
			map.put(cn.getName(), cn);
		}
		return map;
	}

	public static ClassNode create(Class<?> clazz)  {
		try {
			return create(clazz.getName());
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}

	public static ClassNode create(byte[] bytes) {
		return create(bytes, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
	}

	public static ClassNode create(byte[] bytes, int flags) {
		ClassReader reader = new ClassReader(bytes);
		org.objectweb.asm.tree.ClassNode node = new org.objectweb.asm.tree.ClassNode();
		ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9, node) {
			@Override public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				return new JSRInlinerAdapter(super.visitMethod(access, name, desc, signature, exceptions), access, name, desc, signature, exceptions);
			}
		};

		reader.accept(visitor, flags);
		return node;
	}

	public static ClassNode create(InputStream in, int flags) throws IOException {
		return create(readStream(in, true), flags);
	}

	public static ClassNode create(InputStream in) throws IOException {
		return create(in, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
	}

	/**
	 * Reads the given input stream and returns its content as a byte array.
	 *
	 * @param inputStream an input stream.
	 * @param close       true to close the input stream after reading.
	 * @return the content of the given input stream.
	 * @throws IOException if a problem occurs during reading.
	 */
	private static byte[] readStream(final InputStream inputStream, final boolean close)
			throws IOException {
		if (inputStream == null) {
			throw new IOException("Class not found");
		}
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			byte[] data = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
				outputStream.write(data, 0, bytesRead);
			}
			outputStream.flush();
			return outputStream.toByteArray();
		} finally {
			if (close) {
				inputStream.close();
			}
		}
	}

	public static ClassNode create(String name) throws IOException {
		return create(name, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
	}

	public static ClassNode create(String name, int flags) throws IOException {
		return create(ClassLoader.getSystemResourceAsStream(name.replace(".", "/") + ".class"), flags);
	}

	public static void dump(ClassNode cn, OutputStream outputStream) throws IOException {
		outputStream.write(toByteArray(cn));
	}

	public static byte[] toByteArray(ClassNode cn) {
		return toByteArray(cn, ClassWriter.COMPUTE_FRAMES);
	}

	public static  byte[] toByteArray(ClassNode cn, int flags) {
     	ClassWriter writer = new ClassWriter(flags);
		cn.accept(writer);
		return writer.toByteArray();
	}
}
