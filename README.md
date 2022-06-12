# Bangkit-C22-PS032-Product-Based-Capstone
This is repository for Product Based Capstone Bangkit Academy 2022
## Hello 
This our repo for Product Base Capstone
## Theme
Tourism, Creative, and Digital Economy
## The Team
| Name  | Bangkit ID | Learning Path |
| ------------- | ------------- | ------------- |
| Muhammad Hanan Azhar Jihaannuriy | M2224W2046 | Machine Learning |
| Sandy Arfian Mubaroq  | M2015F1414 | Machine Learning |
| Anisha Salsa Amalia | M2249F2200 | Machine Learning |
| Auric | A2465F3083 | Mobile Development |
| Evan Kevin | A7005F0464 | Mobile Development |
| Reinaldy Thando | C7005F0463 | Cloud Computing |

## Machine Learning Learning Path (*coming soon*)
Our team use [TF Lite Model Maker](https://www.tensorflow.org/lite/models/modify/model_maker/object_detection) for created model. 
The TensorFlow Lite Model Maker API helping the process of training a TensorFlow Lite models using custom datasets. It uses transfer learning to reduce the amount of training data required and shorten the training time.

#### Install required packages
```
!pip install -q tflite-model-maker
!pip install -q tflite-support
!pip install -q pycocotools
!pip install opencv-python-headless==4.1.2.30
```

#### Import required packages
```
import numpy as np
import os
import zipfile
import cv2

from PIL import Image
from shutil import copyfile
from tflite_model_maker.config import ExportFormat
from tflite_model_maker import model_spec
from tflite_model_maker import object_detector
from google.colab import drive

import tensorflow as tf
assert tf.__version__.startswith('2')

tf.get_logger().setLevel('ERROR')
from absl import logging
logging.set_verbosity(logging.ERROR)
```

#### Prepare The Dataset

TF Lite model maker API for object detection offer two method for loading dataset.
  - from_csv method
  - from_pascal_voc

Our team use from_pascal_voc method. We found images from public dataset [kaggle](https://www.kaggle.com/datasets/samygrisard/finder-patterns-qr-code). In order to annoted dataset, we use [Roboflow](https://roboflow.com/) . 

Here some steps to load dataset :
1.  Our team saved our file contain dataset at Google Drive. First step is mount dataset from Google Drive
2.  Specify path that have file dataset inside, store path in varible 'souce'. Within folder dataset contain a pair of file consisting of jpg and xml.
3. Split that files to train, validate, and test.
4. Once we have done split, we ready to move to next step.

```
drive.mount('/content/drive')
```

```
source = '/content/drive/My Drive/CAPSTONE-ML/Dataset_FinderPattern/data-qr/' #PATH source from google drive
print(len(os.listdir(source)))
```

#### Examples of dataset
```
import glob
import matplotlib.pyplot as plt
seed = np.random.randint
images=[cv2.imread(image) for image in glob.glob('/content/drive/My Drive/CAPSTONE-ML/Dataset_FinderPattern/data-qr/*jpg')]
for i in range(4):
  plt.imshow(images[i])
  plt.show()
```

####  Split Dataset to Train/Validation/Test
```
#Split dataset to train and test
import random # untuk mengacak dataset(jika membutuhkan acak)
from shutil import copyfile

#Buat folder untuk train dan test
train = os.mkdir('/content/TRAIN')
validation = os.mkdir('/content/VALIDATION')
test = os.mkdir('/content/TEST')

files = []
SPLIT_SIZE = 0.8 #perbandingan train-validation-test adalah 8:1:1

SOURCE = '/content/drive/MyDrive/CAPSTONE-ML/DATA SETTT/data-qr/'
TRAIN = '/content/TRAIN/'
VALIDATION = '/content/VALIDATION/'
TEST = '/content/TEST/'


for filename in os.listdir(SOURCE):
  files.append(filename)
files = sorted(files)
training_length = int(len(files) * SPLIT_SIZE) #panjang dari training
validation_length = int((len(files) - training_length)/2) # panjang dari validation
testing_length = int((len(files) - training_length)/2) #panjang dari test

#shuffled_set = random.sample(files, len(files))

training_set = files[:training_length]
validation_set = files[training_length:training_length+validation_length]
testing_set = files[training_length+validation_length:]


for filename in training_set:
  this_file = SOURCE + filename
  destination = TRAIN + filename
  copyfile(this_file, destination)

for filename in validation_set:
  this_file = SOURCE + filename
  destination = VALIDATION + filename
  copyfile(this_file, destination)

for filename in testing_set:
  this_file = SOURCE + filename
  destination = TEST + filename
  copyfile(this_file, destination)
```

#### Cek Size For Each Folder
```
print('\nTRAIN :\n')
print(len(os.listdir(TRAIN)))

print('\nVALIDATION :\n')
print(len(os.listdir(VALIDATION)))

print('\nTEST :\n')
print(len(os.listdir(TEST)))

```

#### Load Dataset. For load data from PASCAL VOC is used for object detection and its representation as XML files
```
train_data = object_detector.DataLoader.from_pascal_voc('/content/TRAIN', '/content/TRAIN', label_map={1: "0"}) #label map for labeling 1 categories (qr code) and the key value is for dataset name
validation_data = object_detector.DataLoader.from_pascal_voc('/content/VALIDATION', '/content/VALIDATION', label_map={1: "0"})
test_data = object_detector.DataLoader.from_pascal_voc('/content/TEST', '/content/TEST', label_map={1: "0"})
```

#### Transfer Learning
TF Lite model maker offer transfer learning the EfficientDet series.

EfficientDet series ranging from EfficientDet Lite 0 to 4. EfficientDet-Lite are a group of mobile/IoT-friendly object detection models derived from the [EfficientDet](https://arxiv.org/abs/1911.09070) architecture.

Here is the different performance of each EfficientDet-Lite models compared to each others.

| Model architecture | Size(MB)* | Latency(ms)** | Average Precision*** |
|--------------------|-----------|---------------|----------------------|
| EfficientDet-Lite0 | 4.4       | 37            | 25.69%               |
| EfficientDet-Lite1 | 5.8       | 49            | 30.55%               |
| EfficientDet-Lite2 | 7.2       | 69            | 33.97%               |
| EfficientDet-Lite3 | 11.4      | 116           | 37.70%               |
| EfficientDet-Lite4 | 19.9      | 260           | 41.96%               |

<i> * Size of the integer quantized models. <br/>
** Latency measured on Pixel 4 using 4 threads on CPU. <br/>
*** Average Precision is the mAP (mean Average Precision) on the COCO 2017 validation dataset.
</i>

Our team decided to use EfficientDet-Lite2
```
spec = model_spec.get('efficientdet_lite2')
```

#### Start Train
```
model_qrcode = object_detector.create(train_data=train_data, #train dataset
                                      model_spec=spec, #model specification
                                      epochs=1, # number of training epoch
                                      batch_size=16, #batch size
                                      train_whole_model=True, #Train whole model or just layers that are not freezed
                                      validation_data=validation_data) #validation data
```
##### Ouput
```
		Epoch 1/50
		78/78 [==============================] - 123s 933ms/step - det_loss: 0.9238 - cls_loss: 0.5814 - box_loss: 0.0068 - reg_l2_loss: 0.0759 - loss: 0.9997 - learning_rate: 0.0140 - gradient_norm: 2.0209 - val_det_loss: 0.6402 - val_cls_loss: 0.3925 - val_box_loss: 0.0050 - val_reg_l2_loss: 0.0760 - val_loss: 0.7162
		Epoch 2/50
		78/78 [==============================] - 71s 908ms/step - det_loss: 0.3097 - cls_loss: 0.2051 - box_loss: 0.0021 - reg_l2_loss: 0.0761 - loss: 0.3858 - learning_rate: 0.0200 - gradient_norm: 2.0079 - val_det_loss: 0.5049 - val_cls_loss: 0.3235 - val_box_loss: 0.0036 - val_reg_l2_loss: 0.0762 - val_loss: 0.5811
		Epoch 3/50
		78/78 [==============================] - 73s 942ms/step - det_loss: 0.2568 - cls_loss: 0.1742 - box_loss: 0.0017 - reg_l2_loss: 0.0762 - loss: 0.3331 - learning_rate: 0.0199 - gradient_norm: 1.8292 - val_det_loss: 0.4402 - val_cls_loss: 0.2712 - val_box_loss: 0.0034 - val_reg_l2_loss: 0.0763 - val_loss: 0.5165
		Epoch 4/50
		78/78 [==============================] - 73s 935ms/step - det_loss: 0.2463 - cls_loss: 0.1665 - box_loss: 0.0016 - reg_l2_loss: 0.0763 - loss: 0.3226 - learning_rate: 0.0197 - gradient_norm: 1.7250 - val_det_loss: 0.3923 - val_cls_loss: 0.2506 - val_box_loss: 0.0028 - val_reg_l2_loss: 0.0763 - val_loss: 0.4687
		Epoch 5/50
		78/78 [==============================] - 82s 1s/step - det_loss: 0.2242 - cls_loss: 0.1527 - box_loss: 0.0014 - reg_l2_loss: 0.0764 - loss: 0.3006 - learning_rate: 0.0196 - gradient_norm: 1.3862 - val_det_loss: 0.3678 - val_cls_loss: 0.2183 - val_box_loss: 0.0030 - val_reg_l2_loss: 0.0764 - val_loss: 0.4442
		Epoch 6/50
		78/78 [==============================] - 72s 930ms/step - det_loss: 0.2231 - cls_loss: 0.1556 - box_loss: 0.0013 - reg_l2_loss: 0.0764 - loss: 0.2995 - learning_rate: 0.0194 - gradient_norm: 1.5147 - val_det_loss: 0.3449 - val_cls_loss: 0.2467 - val_box_loss: 0.0020 - val_reg_l2_loss: 0.0764 - val_loss: 0.4213
		Epoch 7/50
		78/78 [==============================] - 74s 948ms/step - det_loss: 0.2079 - cls_loss: 0.1457 - box_loss: 0.0012 - reg_l2_loss: 0.0764 - loss: 0.2843 - learning_rate: 0.0191 - gradient_norm: 1.3904 - val_det_loss: 0.3088 - val_cls_loss: 0.1898 - val_box_loss: 0.0024 - val_reg_l2_loss: 0.0764 - val_loss: 0.3852
		Epoch 8/50
		78/78 [==============================] - 73s 937ms/step - det_loss: 0.2064 - cls_loss: 0.1413 - box_loss: 0.0013 - reg_l2_loss: 0.0765 - loss: 0.2829 - learning_rate: 0.0189 - gradient_norm: 1.4590 - val_det_loss: 0.3025 - val_cls_loss: 0.2027 - val_box_loss: 0.0020 - val_reg_l2_loss: 0.0765 - val_loss: 0.3790
		Epoch 9/50
		78/78 [==============================] - 72s 928ms/step - det_loss: 0.1983 - cls_loss: 0.1375 - box_loss: 0.0012 - reg_l2_loss: 0.0765 - loss: 0.2748 - learning_rate: 0.0185 - gradient_norm: 1.3832 - val_det_loss: 0.2909 - val_cls_loss: 0.1987 - val_box_loss: 0.0018 - val_reg_l2_loss: 0.0765 - val_loss: 0.3674
		Epoch 10/50
		78/78 [==============================] - 77s 993ms/step - det_loss: 0.1925 - cls_loss: 0.1355 - box_loss: 0.0011 - reg_l2_loss: 0.0765 - loss: 0.2690 - learning_rate: 0.0182 - gradient_norm: 1.3575 - val_det_loss: 0.3202 - val_cls_loss: 0.2144 - val_box_loss: 0.0021 - val_reg_l2_loss: 0.0765 - val_loss: 0.3966
		Epoch 11/50
		78/78 [==============================] - 72s 931ms/step - det_loss: 0.1914 - cls_loss: 0.1344 - box_loss: 0.0011 - reg_l2_loss: 0.0765 - loss: 0.2678 - learning_rate: 0.0178 - gradient_norm: 1.3362 - val_det_loss: 0.3511 - val_cls_loss: 0.1999 - val_box_loss: 0.0030 - val_reg_l2_loss: 0.0765 - val_loss: 0.4276
		Epoch 12/50
		78/78 [==============================] - 72s 929ms/step - det_loss: 0.1880 - cls_loss: 0.1268 - box_loss: 0.0012 - reg_l2_loss: 0.0765 - loss: 0.2645 - learning_rate: 0.0174 - gradient_norm: 1.2942 - val_det_loss: 0.2974 - val_cls_loss: 0.2076 - val_box_loss: 0.0018 - val_reg_l2_loss: 0.0765 - val_loss: 0.3738
		Epoch 13/50
		78/78 [==============================] - 73s 938ms/step - det_loss: 0.1789 - cls_loss: 0.1257 - box_loss: 0.0011 - reg_l2_loss: 0.0765 - loss: 0.2554 - learning_rate: 0.0170 - gradient_norm: 1.3345 - val_det_loss: 0.3435 - val_cls_loss: 0.2105 - val_box_loss: 0.0027 - val_reg_l2_loss: 0.0765 - val_loss: 0.4200
		Epoch 14/50
		78/78 [==============================] - 73s 937ms/step - det_loss: 0.1743 - cls_loss: 0.1243 - box_loss: 9.9974e-04 - reg_l2_loss: 0.0765 - loss: 0.2507 - learning_rate: 0.0165 - gradient_norm: 1.2572 - val_det_loss: 0.2934 - val_cls_loss: 0.2003 - val_box_loss: 0.0019 - val_reg_l2_loss: 0.0764 - val_loss: 0.3698
		Epoch 15/50
		78/78 [==============================] - 77s 986ms/step - det_loss: 0.1730 - cls_loss: 0.1249 - box_loss: 9.6239e-04 - reg_l2_loss: 0.0764 - loss: 0.2495 - learning_rate: 0.0160 - gradient_norm: 1.2346 - val_det_loss: 0.3201 - val_cls_loss: 0.2161 - val_box_loss: 0.0021 - val_reg_l2_loss: 0.0764 - val_loss: 0.3966
		Epoch 16/50
		78/78 [==============================] - 73s 933ms/step - det_loss: 0.1726 - cls_loss: 0.1243 - box_loss: 9.6682e-04 - reg_l2_loss: 0.0764 - loss: 0.2490 - learning_rate: 0.0155 - gradient_norm: 1.2581 - val_det_loss: 0.3612 - val_cls_loss: 0.2404 - val_box_loss: 0.0024 - val_reg_l2_loss: 0.0764 - val_loss: 0.4377
		Epoch 17/50
		78/78 [==============================] - 73s 938ms/step - det_loss: 0.1699 - cls_loss: 0.1229 - box_loss: 9.4002e-04 - reg_l2_loss: 0.0764 - loss: 0.2463 - learning_rate: 0.0149 - gradient_norm: 1.1958 - val_det_loss: 0.2727 - val_cls_loss: 0.2130 - val_box_loss: 0.0012 - val_reg_l2_loss: 0.0764 - val_loss: 0.3491
		Epoch 18/50
		78/78 [==============================] - 72s 926ms/step - det_loss: 0.1644 - cls_loss: 0.1157 - box_loss: 9.7402e-04 - reg_l2_loss: 0.0764 - loss: 0.2408 - learning_rate: 0.0143 - gradient_norm: 1.2442 - val_det_loss: 0.3409 - val_cls_loss: 0.2578 - val_box_loss: 0.0017 - val_reg_l2_loss: 0.0764 - val_loss: 0.4172
		Epoch 19/50
		78/78 [==============================] - 73s 934ms/step - det_loss: 0.1599 - cls_loss: 0.1165 - box_loss: 8.6826e-04 - reg_l2_loss: 0.0764 - loss: 0.2363 - learning_rate: 0.0138 - gradient_norm: 1.3269 - val_det_loss: 0.3767 - val_cls_loss: 0.2724 - val_box_loss: 0.0021 - val_reg_l2_loss: 0.0764 - val_loss: 0.4530
		Epoch 20/50
		78/78 [==============================] - 77s 987ms/step - det_loss: 0.1631 - cls_loss: 0.1172 - box_loss: 9.1852e-04 - reg_l2_loss: 0.0764 - loss: 0.2394 - learning_rate: 0.0132 - gradient_norm: 1.2207 - val_det_loss: 0.3046 - val_cls_loss: 0.2236 - val_box_loss: 0.0016 - val_reg_l2_loss: 0.0763 - val_loss: 0.3810
		Epoch 21/50
		78/78 [==============================] - 72s 929ms/step - det_loss: 0.1541 - cls_loss: 0.1109 - box_loss: 8.6472e-04 - reg_l2_loss: 0.0763 - loss: 0.2305 - learning_rate: 0.0125 - gradient_norm: 1.1925 - val_det_loss: 0.3659 - val_cls_loss: 0.2639 - val_box_loss: 0.0020 - val_reg_l2_loss: 0.0763 - val_loss: 0.4422
		Epoch 22/50
		78/78 [==============================] - 72s 927ms/step - det_loss: 0.1518 - cls_loss: 0.1083 - box_loss: 8.7043e-04 - reg_l2_loss: 0.0763 - loss: 0.2281 - learning_rate: 0.0119 - gradient_norm: 1.2213 - val_det_loss: 0.3352 - val_cls_loss: 0.2408 - val_box_loss: 0.0019 - val_reg_l2_loss: 0.0763 - val_loss: 0.4115
		Epoch 23/50
		78/78 [==============================] - 74s 947ms/step - det_loss: 0.1444 - cls_loss: 0.1054 - box_loss: 7.8134e-04 - reg_l2_loss: 0.0763 - loss: 0.2207 - learning_rate: 0.0113 - gradient_norm: 1.2094 - val_det_loss: 0.3026 - val_cls_loss: 0.2256 - val_box_loss: 0.0015 - val_reg_l2_loss: 0.0763 - val_loss: 0.3789
		Epoch 24/50
		78/78 [==============================] - 75s 956ms/step - det_loss: 0.1529 - cls_loss: 0.1109 - box_loss: 8.3980e-04 - reg_l2_loss: 0.0763 - loss: 0.2292 - learning_rate: 0.0106 - gradient_norm: 1.3152 - val_det_loss: 0.3665 - val_cls_loss: 0.2784 - val_box_loss: 0.0018 - val_reg_l2_loss: 0.0763 - val_loss: 0.4428
		Epoch 25/50
		78/78 [==============================] - 76s 980ms/step - det_loss: 0.1469 - cls_loss: 0.1083 - box_loss: 7.7305e-04 - reg_l2_loss: 0.0763 - loss: 0.2232 - learning_rate: 0.0100 - gradient_norm: 1.2493 - val_det_loss: 0.2908 - val_cls_loss: 0.2111 - val_box_loss: 0.0016 - val_reg_l2_loss: 0.0762 - val_loss: 0.3670
		Epoch 26/50
		78/78 [==============================] - 72s 923ms/step - det_loss: 0.1390 - cls_loss: 0.1020 - box_loss: 7.4058e-04 - reg_l2_loss: 0.0762 - loss: 0.2152 - learning_rate: 0.0094 - gradient_norm: 1.1572 - val_det_loss: 0.3425 - val_cls_loss: 0.2436 - val_box_loss: 0.0020 - val_reg_l2_loss: 0.0762 - val_loss: 0.4187
		Epoch 27/50
		78/78 [==============================] - 74s 950ms/step - det_loss: 0.1429 - cls_loss: 0.1048 - box_loss: 7.6240e-04 - reg_l2_loss: 0.0762 - loss: 0.2191 - learning_rate: 0.0087 - gradient_norm: 1.2667 - val_det_loss: 0.3606 - val_cls_loss: 0.2415 - val_box_loss: 0.0024 - val_reg_l2_loss: 0.0762 - val_loss: 0.4368
		Epoch 28/50
		78/78 [==============================] - 72s 922ms/step - det_loss: 0.1388 - cls_loss: 0.1022 - box_loss: 7.3127e-04 - reg_l2_loss: 0.0762 - loss: 0.2150 - learning_rate: 0.0081 - gradient_norm: 1.2357 - val_det_loss: 0.2498 - val_cls_loss: 0.1942 - val_box_loss: 0.0011 - val_reg_l2_loss: 0.0762 - val_loss: 0.3260
		Epoch 29/50
		78/78 [==============================] - 72s 924ms/step - det_loss: 0.1400 - cls_loss: 0.1026 - box_loss: 7.4793e-04 - reg_l2_loss: 0.0762 - loss: 0.2162 - learning_rate: 0.0075 - gradient_norm: 1.2824 - val_det_loss: 0.2759 - val_cls_loss: 0.2092 - val_box_loss: 0.0013 - val_reg_l2_loss: 0.0762 - val_loss: 0.3521
		Epoch 30/50
		78/78 [==============================] - 77s 986ms/step - det_loss: 0.1333 - cls_loss: 0.0994 - box_loss: 6.7868e-04 - reg_l2_loss: 0.0762 - loss: 0.2095 - learning_rate: 0.0068 - gradient_norm: 1.2866 - val_det_loss: 0.3055 - val_cls_loss: 0.2048 - val_box_loss: 0.0020 - val_reg_l2_loss: 0.0761 - val_loss: 0.3816
		Epoch 31/50
		78/78 [==============================] - 72s 923ms/step - det_loss: 0.1372 - cls_loss: 0.1026 - box_loss: 6.9249e-04 - reg_l2_loss: 0.0761 - loss: 0.2134 - learning_rate: 0.0062 - gradient_norm: 1.3418 - val_det_loss: 0.3092 - val_cls_loss: 0.2072 - val_box_loss: 0.0020 - val_reg_l2_loss: 0.0761 - val_loss: 0.3853
		Epoch 32/50
		78/78 [==============================] - 73s 931ms/step - det_loss: 0.1278 - cls_loss: 0.0970 - box_loss: 6.1683e-04 - reg_l2_loss: 0.0761 - loss: 0.2039 - learning_rate: 0.0057 - gradient_norm: 1.2260 - val_det_loss: 0.2811 - val_cls_loss: 0.2032 - val_box_loss: 0.0016 - val_reg_l2_loss: 0.0761 - val_loss: 0.3572
		Epoch 33/50
		78/78 [==============================] - 74s 949ms/step - det_loss: 0.1296 - cls_loss: 0.0978 - box_loss: 6.3531e-04 - reg_l2_loss: 0.0761 - loss: 0.2057 - learning_rate: 0.0051 - gradient_norm: 1.2715 - val_det_loss: 0.2802 - val_cls_loss: 0.2001 - val_box_loss: 0.0016 - val_reg_l2_loss: 0.0761 - val_loss: 0.3563
		Epoch 34/50
		78/78 [==============================] - 72s 925ms/step - det_loss: 0.1277 - cls_loss: 0.0958 - box_loss: 6.3827e-04 - reg_l2_loss: 0.0761 - loss: 0.2038 - learning_rate: 0.0045 - gradient_norm: 1.3033 - val_det_loss: 0.2862 - val_cls_loss: 0.2045 - val_box_loss: 0.0016 - val_reg_l2_loss: 0.0761 - val_loss: 0.3623
		Epoch 35/50
		78/78 [==============================] - 76s 969ms/step - det_loss: 0.1286 - cls_loss: 0.0973 - box_loss: 6.2628e-04 - reg_l2_loss: 0.0761 - loss: 0.2047 - learning_rate: 0.0040 - gradient_norm: 1.2520 - val_det_loss: 0.2889 - val_cls_loss: 0.2088 - val_box_loss: 0.0016 - val_reg_l2_loss: 0.0761 - val_loss: 0.3650
		Epoch 36/50
		78/78 [==============================] - 73s 936ms/step - det_loss: 0.1262 - cls_loss: 0.0948 - box_loss: 6.2730e-04 - reg_l2_loss: 0.0761 - loss: 0.2023 - learning_rate: 0.0035 - gradient_norm: 1.2783 - val_det_loss: 0.2810 - val_cls_loss: 0.2021 - val_box_loss: 0.0016 - val_reg_l2_loss: 0.0761 - val_loss: 0.3571
		Epoch 37/50
		78/78 [==============================] - 72s 927ms/step - det_loss: 0.1230 - cls_loss: 0.0924 - box_loss: 6.1234e-04 - reg_l2_loss: 0.0761 - loss: 0.1990 - learning_rate: 0.0030 - gradient_norm: 1.3022 - val_det_loss: 0.2858 - val_cls_loss: 0.2036 - val_box_loss: 0.0016 - val_reg_l2_loss: 0.0760 - val_loss: 0.3618
		Epoch 38/50
		78/78 [==============================] - 72s 923ms/step - det_loss: 0.1276 - cls_loss: 0.0962 - box_loss: 6.2840e-04 - reg_l2_loss: 0.0760 - loss: 0.2036 - learning_rate: 0.0026 - gradient_norm: 1.2450 - val_det_loss: 0.2997 - val_cls_loss: 0.2171 - val_box_loss: 0.0017 - val_reg_l2_loss: 0.0760 - val_loss: 0.3758
		Epoch 39/50
		78/78 [==============================] - 72s 923ms/step - det_loss: 0.1227 - cls_loss: 0.0917 - box_loss: 6.2155e-04 - reg_l2_loss: 0.0760 - loss: 0.1988 - learning_rate: 0.0022 - gradient_norm: 1.2219 - val_det_loss: 0.2871 - val_cls_loss: 0.2021 - val_box_loss: 0.0017 - val_reg_l2_loss: 0.0760 - val_loss: 0.3632
		Epoch 40/50
		78/78 [==============================] - 77s 991ms/step - det_loss: 0.1182 - cls_loss: 0.0894 - box_loss: 5.7652e-04 - reg_l2_loss: 0.0760 - loss: 0.1942 - learning_rate: 0.0018 - gradient_norm: 1.1870 - val_det_loss: 0.2838 - val_cls_loss: 0.2086 - val_box_loss: 0.0015 - val_reg_l2_loss: 0.0760 - val_loss: 0.3599
		Epoch 41/50
		78/78 [==============================] - 74s 944ms/step - det_loss: 0.1147 - cls_loss: 0.0861 - box_loss: 5.7175e-04 - reg_l2_loss: 0.0760 - loss: 0.1907 - learning_rate: 0.0015 - gradient_norm: 1.2415 - val_det_loss: 0.2812 - val_cls_loss: 0.2076 - val_box_loss: 0.0015 - val_reg_l2_loss: 0.0760 - val_loss: 0.3572
		Epoch 42/50
		78/78 [==============================] - 72s 924ms/step - det_loss: 0.1173 - cls_loss: 0.0887 - box_loss: 5.7170e-04 - reg_l2_loss: 0.0760 - loss: 0.1933 - learning_rate: 0.0011 - gradient_norm: 1.2440 - val_det_loss: 0.2813 - val_cls_loss: 0.2084 - val_box_loss: 0.0015 - val_reg_l2_loss: 0.0760 - val_loss: 0.3573
		Epoch 43/50
		78/78 [==============================] - 73s 941ms/step - det_loss: 0.1143 - cls_loss: 0.0858 - box_loss: 5.6868e-04 - reg_l2_loss: 0.0760 - loss: 0.1903 - learning_rate: 8.5752e-04 - gradient_norm: 1.1935 - val_det_loss: 0.2840 - val_cls_loss: 0.2117 - val_box_loss: 0.0014 - val_reg_l2_loss: 0.0760 - val_loss: 0.3600
		Epoch 44/50
		78/78 [==============================] - 73s 936ms/step - det_loss: 0.1134 - cls_loss: 0.0848 - box_loss: 5.7246e-04 - reg_l2_loss: 0.0760 - loss: 0.1894 - learning_rate: 6.1700e-04 - gradient_norm: 1.1975 - val_det_loss: 0.2943 - val_cls_loss: 0.2163 - val_box_loss: 0.0016 - val_reg_l2_loss: 0.0760 - val_loss: 0.3703
		Epoch 45/50
		78/78 [==============================] - 76s 976ms/step - det_loss: 0.1135 - cls_loss: 0.0848 - box_loss: 5.7392e-04 - reg_l2_loss: 0.0760 - loss: 0.1895 - learning_rate: 4.1503e-04 - gradient_norm: 1.1628 - val_det_loss: 0.2914 - val_cls_loss: 0.2135 - val_box_loss: 0.0016 - val_reg_l2_loss: 0.0760 - val_loss: 0.3674
		Epoch 46/50
		78/78 [==============================] - 73s 932ms/step - det_loss: 0.1179 - cls_loss: 0.0884 - box_loss: 5.9072e-04 - reg_l2_loss: 0.0760 - loss: 0.1939 - learning_rate: 2.5245e-04 - gradient_norm: 1.2239 - val_det_loss: 0.2927 - val_cls_loss: 0.2160 - val_box_loss: 0.0015 - val_reg_l2_loss: 0.0760 - val_loss: 0.3688
		Epoch 47/50
		78/78 [==============================] - 71s 913ms/step - det_loss: 0.1158 - cls_loss: 0.0883 - box_loss: 5.4928e-04 - reg_l2_loss: 0.0760 - loss: 0.1918 - learning_rate: 1.2993e-04 - gradient_norm: 1.2800 - val_det_loss: 0.2931 - val_cls_loss: 0.2159 - val_box_loss: 0.0015 - val_reg_l2_loss: 0.0760 - val_loss: 0.3691
		Epoch 48/50
		78/78 [==============================] - 71s 908ms/step - det_loss: 0.1141 - cls_loss: 0.0856 - box_loss: 5.7117e-04 - reg_l2_loss: 0.0760 - loss: 0.1901 - learning_rate: 4.7964e-05 - gradient_norm: 1.1523 - val_det_loss: 0.2938 - val_cls_loss: 0.2168 - val_box_loss: 0.0015 - val_reg_l2_loss: 0.0760 - val_loss: 0.3698
		Epoch 49/50
		78/78 [==============================] - 71s 909ms/step - det_loss: 0.1128 - cls_loss: 0.0860 - box_loss: 5.3623e-04 - reg_l2_loss: 0.0760 - loss: 0.1888 - learning_rate: 6.8935e-06 - gradient_norm: 1.1994 - val_det_loss: 0.2940 - val_cls_loss: 0.2165 - val_box_loss: 0.0016 - val_reg_l2_loss: 0.0760 - val_loss: 0.3700
		Epoch 50/50
		78/78 [==============================] - 75s 968ms/step - det_loss: 0.1183 - cls_loss: 0.0904 - box_loss: 5.5807e-04 - reg_l2_loss: 0.0760 - loss: 0.1943 - learning_rate: 6.8869e-06 - gradient_norm: 1.2843 - val_det_loss: 0.2928 - val_cls_loss: 0.2158 - val_box_loss: 0.0015 - val_reg_l2_loss: 0.0760 - val_loss: 0.3688

```

#### Evalute Model With Test Data
```
model_qrcode.evaluate(test_data)
```
##### Output
```

		{'AP': 0.7964094,
		 'AP50': 0.97167325,
		 'AP75': 0.91286486,
		 'AP_/0': 0.7964094,
		 'APl': 0.84294975,
		 'APm': 0.58701664,
		 'APs': 0.21166563,
		 'ARl': 0.8992366,
		 'ARm': 0.7,
		 'ARmax1': 0.80700636,
		 'ARmax10': 0.8579618,
		 'ARmax100': 0.8649682,
		 'ARs': 0.65}
```

#### Save model in TF Lite
```
model_qrcode.export(export_dir='/content/model/.', tflite_filename='model_qrcode.tflite') #for exporting data from directory, ('/content/model/.') dot -> to pull all data
```

#### After export Model to TensorFlow lite you'll have to evaluate the exported TFLite model
```
	Stream
		157/157 [==============================] - 1236s 8s/step
		{'AP': 0.7800725,
		 'AP50': 0.9836855,
		 'AP75': 0.9067603,
		 'AP_/0': 0.7800725,
		 'APl': 0.82469314,
		 'APm': 0.5782041,
		 'APs': 0.21452145,
		 'ARl': 0.86030537,
		 'ARm': 0.6545454,
		 'ARmax1': 0.7949045,
		 'ARmax10': 0.8191083,
		 'ARmax100': 0.822293,
		 'ARs': 0.5}
```

#### Evaluate TF Lite Model
Test TF Lite Model on Images (Source Code from
```
model_path = '/content/model/model_qrcode.tflite'

# Load the labels into a list
classes = ['???'] * model_qrcode.model_spec.config.num_classes
label_map = model_qrcode.model_spec.config.label_map
for label_id, label_name in label_map.as_dict().items():
  classes[label_id-1] = label_name

# Define a list of colors for visualization
colors = np.random.randint(0, 255, size=(len(classes), 3), dtype=np.uint8) #generating random color
```

```
def detect_objects(interpreter, image, threshold):
  """Returns a list of detection results, each a dictionary of object info."""

  signature_fn = interpreter.get_signature_runner()

  # Feed the input image to the model
  output = signature_fn(images=image)

  # Get all outputs from the model
  count = int(np.squeeze(output['output_0']))
  scores = np.squeeze(output['output_1'])
  classes = np.squeeze(output['output_2'])
  boxes = np.squeeze(output['output_3'])

  results = []
  for i in range(count):
    if scores[i] >= threshold:
      result = {'bounding_box': boxes[i], 'class_id': classes[i], 'score': scores[i]}
      results.append(result)
  return results

def preprocess_image(image_path, input_size):
  """Preprocess the input image to feed to the TFLite model"""
  img = tf.io.read_file(image_path)
  img = tf.io.decode_image(img, channels=3)
  img = tf.image.convert_image_dtype(img, tf.uint8)
  original_image = img
  resized_img = tf.image.resize(img, input_size)
  resized_img = resized_img[tf.newaxis, :]
  resized_img = tf.cast(resized_img, dtype=tf.uint8)
  return resized_img, original_image
```

```
def run_odt_and_draw_results(image_path, interpreter, threshold=0.5):
  """Run object detection on the input image and draw the detection results"""
  # Load the input shape required by the model
  _, input_height, input_width, _ = interpreter.get_input_details()[0]['shape']

  # Load the input image and preprocess it
  preprocessed_image, original_image = preprocess_image(
      image_path,
      (input_height, input_width)
    )

  # Run object detection on the input image
  results = detect_objects(interpreter, preprocessed_image, threshold=threshold)

  # Plot the detection results on the input image
  original_image_np = original_image.numpy().astype(np.uint8)
  for obj in results:
    # Convert the object bounding box from relative coordinates to absolute
    # coordinates based on the original image resolution
    ymin, xmin, ymax, xmax = obj['bounding_box']
    xmin = int(xmin * original_image_np.shape[1])
    xmax = int(xmax * original_image_np.shape[1])
    ymin = int(ymin * original_image_np.shape[0])
    ymax = int(ymax * original_image_np.shape[0])

    # Find the class index of the current object
    class_id = int(obj['class_id'])

    # Draw the bounding box and label on the image
    color = [int(c) for c in colors[class_id]]
    #color = [int(c)]
    #for c in colors[class_id]:
      #color = [int(c)]
    cv2.rectangle(original_image_np, (xmin, ymin), (xmax, ymax), color, 2)
    # Make adjustments to make the label visible for all objects
    y = ymin - 15 if ymin - 15 > 15 else ymin + 15
    label = "{}: {:.0f}%".format(classes[class_id], obj['score'] * 100)
    cv2.putText(original_image_np, label, (xmin, y),
        cv2.FONT_HERSHEY_SIMPLEX, 0.5, color, 2)

  # Return the final image
  original_uint8 = original_image_np.astype(np.uint8)
  return original_uint8
```

```
INPUT_IMAGE_URL = "https://t-2.tstatic.net/tribunjatimtravel/foto/bank/images/Pengunjung-mall-wajib-menunjukan-bukti-vaksin-menggunakan-aplikasi-PeduliLindungi.jpg" #@param {type:"string"}
DETECTION_THRESHOLD = 0.3 #@param {type:"string"}

TEMP_FILE = '/tmp/image.png'

!wget -q -O $TEMP_FILE $INPUT_IMAGE_URL
im = Image.open(TEMP_FILE)
print(im)
im.thumbnail((512, 512), Image.ANTIALIAS)
im.save(TEMP_FILE, 'PNG')

# Load the TFLite model
interpreter = tf.lite.Interpreter(model_path=model_path)
interpreter.allocate_tensors()

# Run inference and draw detection result on the local copy of the original file
detection_result_image = run_odt_and_draw_results(
    TEMP_FILE,
    interpreter,
    threshold=DETECTION_THRESHOLD
)

# Show the detection result
Image.fromarray(detection_result_image
```
## Mobile Development Learning Path (*coming soon*)
TBA.

## Cloud Computing Learning Path (*coming soon*)
TBA.
