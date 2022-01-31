.class public Output 
.super java/lang/Object

.method public <init>()V
 aload_0
 invokenonvirtual java/lang/Object/<init>()V
 return
.end method

.method public static print(I)V
 .limit stack 2
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 invokestatic java/lang/Integer/toString(I)Ljava/lang/String;
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static read()I
 .limit stack 3
 new java/util/Scanner
 dup
 getstatic java/lang/System/in Ljava/io/InputStream;
 invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
 invokevirtual java/util/Scanner/next()Ljava/lang/String;
 invokestatic java/lang/Integer.parseInt(Ljava/lang/String;)I
 ireturn
.end method

.method public static run()V
 .limit stack 1024
 .limit locals 256
 invokestatic Output/read()I
 istore 0
 goto L1
L1:
 ldc 0
 dup 
 istore 1
 pop 
 goto L2
L2:
 ldc 1
 dup 
 istore 2
 pop 
 goto L3
L3:
 ldc 0
 dup 
 istore 3
 pop 
 goto L4
L4:
 iload 2
 invokestatic Output/print(I)V
 goto L5
L5:
 iload 0
 ldc 1
 isub 
 dup 
 istore 0
 pop 
 goto L6
L6:
L8:
 iload 0
 ldc 0
 if_icmple L9
 iload 0
 ldc 1
 isub 
 dup 
 istore 0
 pop 
 goto L10
L10:
 iload 1
 iload 2
 iadd 
 dup 
 istore 3
 pop 
 goto L11
L11:
 iload 2
 dup 
 istore 1
 pop 
 goto L12
L12:
 iload 3
 dup 
 istore 2
 pop 
 goto L13
L13:
 iload 3
 invokestatic Output/print(I)V
 goto L14
L14:
 goto L8
L9:
 goto L7
L7:
 goto L0
L0:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

