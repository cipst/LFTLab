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
 invokestatic Output/read()I
 istore 1
 invokestatic Output/read()I
 istore 2
 goto L1
L1:
 iload 0
 iload 1
 if_icmpgt L3
 goto L4
L3:
 iload 0
 iload 2
 if_icmpgt L6
 goto L7
L6:
 iload 0
 invokestatic Output/print(I)V
 goto L8
L7:
 iload 2
 invokestatic Output/print(I)V
 goto L8
L8:
 goto L5
L4:
 iload 1
 iload 2
 if_icmpgt L9
 goto L10
L9:
 iload 1
 invokestatic Output/print(I)V
 goto L11
L10:
 iload 2
 invokestatic Output/print(I)V
 goto L11
L11:
 goto L5
L5:
 goto L2
L2:
 ldc 777
 invokestatic Output/print(I)V
 goto L12
L12:
 ldc 10
 dup 
 istore 3
 dup 
 istore 4
 pop 
 goto L13
L13:
 iload 3
 invokestatic Output/print(I)V
 iload 4
 invokestatic Output/print(I)V
 goto L14
L14:
 invokestatic Output/read()I
 istore 0
 invokestatic Output/read()I
 istore 1
 goto L15
L15:
 ldc 1
 invokestatic Output/print(I)V
 ldc 2
 ldc 3
 iadd 
 ldc 4
 iadd 
 invokestatic Output/print(I)V
 goto L16
L16:
 iload 0
 iload 1
 if_icmpgt L18
 goto L19
L18:
 iload 0
 invokestatic Output/print(I)V
 goto L20
L19:
 iload 1
 invokestatic Output/print(I)V
 goto L20
L20:
 goto L17
L17:
L22:
 iload 0
 ldc 0
 if_icmpgt L23
 goto L24
L23:
 iload 0
 ldc 1
 isub 
 dup 
 istore 0
 pop 
 goto L25
L25:
 iload 0
 invokestatic Output/print(I)V
 goto L26
L26:
 goto L22
L24:
 goto L21
L21:
 ldc 333
 invokestatic Output/print(I)V
 goto L27
L27:
 invokestatic Output/read()I
 istore 5
 invokestatic Output/read()I
 istore 6
 goto L28
L28:
L30:
 iload 5
 ldc 0
 if_icmpgt L31
 goto L32
L31:
 iload 5
 invokestatic Output/print(I)V
 goto L33
L33:
 iload 6
 dup 
 istore 7
 pop 
 goto L34
L34:
L36:
 iload 7
 ldc 0
 if_icmpgt L37
 goto L38
L37:
 iload 7
 invokestatic Output/print(I)V
 goto L39
L39:
 iload 7
 ldc 1
 isub 
 dup 
 istore 7
 pop 
 goto L40
L40:
 goto L41
L41:
 goto L36
L38:
 goto L35
L35:
 iload 5
 ldc 1
 isub 
 dup 
 istore 5
 pop 
 goto L42
L42:
 goto L43
L43:
 goto L30
L32:
 goto L29
L29:
 ldc 999
 invokestatic Output/print(I)V
 goto L44
L44:
 goto L45
L45:
 goto L0
L0:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

