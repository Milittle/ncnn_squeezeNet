#  ncnn_squeezeNet android studio demo

1. 这是一个腾讯ncnn squeezeNet的ImageNet分类Android工程 Demo。
2. 如果你感兴趣，可以clone去把玩。
3. 有用的话可以给个star。谢谢~

Demo Show：

![](https://s1.ax1x.com/2018/12/21/FsB0BD.jpg)

### 详细步骤解析：

NCNN android 深度学习部署方案

**一、模型训练**

你可以选择现有的深度学习框架比如caffe、tensorflow、mxnet、pytorch、cntk等框架训练你的模型，但是确保你的op很通用，就是别那么别具一格（个人意见），还有一点需要注意的是，各框架的模型框架中的模型格式各有不同，这样导致在模型同一格式的时候会出现很多问题。所以，你应该尽量管理好op name还有op作用域。根据我经验来讲，不管是部署在android手机端还是云端，这些好的习惯，都会让整个过程很舒服。因为你也不想在这个过程充满痛苦，对不对？

**二、模型转换ncnn支持格式**

这一节会列出一些框架的模型格式，这样可以让你更多的了解深度学习的模型里面会包含什么？

TensorFlow：（我主要了解tf，其他框架不是特别熟悉）

ckpt，这种模型格式是在TensorFlow训练时候保存下来的，包含三个文件：

```
model.meta
model.ckpt.data
model.ckpt.index
```

这三个文件第一个是原图结构，第二个是存储数据文件，第三个是op index。

SavedModel：这是TensorFlow在serving过程中保存的服务模型格式：

```
1-version folder
	model.pb(or model.pbtxt-humanable)
	variable--data folder
		variables.data-00000-of-00001
		variables.index
```

上面的1代表的是模型版本号，这个可以在tf.saved_model模型导出模型的时候指定，其实大家去实际操作一把的话，你就会发现下面的三个文件其实和上面的三个文件大小差不多，只不过是在model.pb里面加入了serving特定的标记位，然后为了方便tf-serving的云端执行。

PB：这个是TensorFLow的冻图模型，这个是一个文件，里面包含了图的信息和数据。这个pb还可以通过onnx-tensorflow转换为onnx模型文件，以及在TensorRT中用到的uff格式的，都是可以从pb模型转换过去。所以说在我们实际工业界部署深度学习模型，这个pb可以实现模型格式的转换（但是我提醒一下，不仅仅是我说的这么简单，因为在实际转换过程中，会出现一些模型op不支持的操作，所以大家不要想的那么理想，可以自己实验一把，就知道这个过程有时候会比较麻烦）。

Caffe：其实说实话，我对caffe不熟，所以我简单说一下：

```
model.prototxt
model.caffemodel
```

上面第一个文件存储图格式，第二个文件存储模型数据。

其他框架实在没有用过，就不献丑了。

**三、模型转换到ncnn格式**

这个过程需要使用ncnn项目下面的tools，官方支持三种模型格式，caffe、mxnet、onnx，作者指出由于tf本身也有tflite，而且tf本身的操作实在太过复杂，所以断掉了tf的维护（哭脸，我打算尝试一下tf-ncnn，毕竟这也是一门技术，探究一下tf的op也是很有必要的）。

编译好ncnn的项目以后，那么你就可以实现从模型到ncnn模型格式的转换，这个过程如果有不支持的操作，那么你可以自己修改源码来增加操作（这个步骤可能很难，我也没有试过），后续可以实验一下。然后你通过这两个工具直接将其他框架的模型文件转换到ncnn，如下文件：

```
model.param----op map table
model.bin----binary data for storing the weights etc.
```

上面第一个文件是模型文件，第二个是参数数据存储文件。你也可以通过tools最外层ncnn2mem工具将model.param转换为纯内存格式，这样别人就不会看到你的模型架构，这样，最起码还有点安全性。

**四、编写android jni调用实例**

打开你的android studio，你要在app/src/main下面创建JNI文件夹，然后里面分别编写android.mk和application.mk文件，然后创建对应的jni cpp调用，这块可以参考以上Demo，然后配置External tool ndk-build构建对应的so文件。

然后创建你的java类，用来调用jni cpp里面的函数。然后就像写Android的app一样，实现你的深度学习在移动端侧带来的魅力。

**五、部署真机测试**

最后，将Debug App部署到你的手机上，加载一张图，看看分类结果（此次使用的是Squeezenet去分类ImageNet的物体）。