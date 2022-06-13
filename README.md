# Bangkit-C22-PS032-Product-Based-Capstone
This is repository for Product Based Capstone Bangkit Academy 2022
## Hello 
This our repo for our team Product-Based Capstone Project. in this project, we developed a payment app called Receh. 
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

## Machine Learning Learning Path
Our team use [TF Lite Model Maker](https://www.tensorflow.org/lite/models/modify/model_maker/object_detection) for created model. 
The TensorFlow Lite Model Maker API helping the process of training a TensorFlow Lite models using custom datasets. It uses transfer learning to reduce the amount of training data required and shorten the training time. We found data from kaggle and labeling manually using roboflow, saved format dataset as a pascal_voc. Transfer Learning that we use is EfficientDet-Lite2 and training for 50 epoch, and we get around 80% AP (Average Precision) . After that saved model as TF Lite
## Mobile Development Learning Path
In working on this application, our team uses the MVVM architecture and the datastore as the base. We also use TF Lite to be able to implement Machine Learning to Android. Apart from TF Lite, we also use Retrofit and also Firebase. Retrofit is used to store or get transaction history and also to get some partner/seller data into API. Firebase is used as the main database and also Authentication for users.

## Cloud Computing Learning Path
We make private API to save and get the data transaction from all user. That, we will use for history function in our application. The API we make it with node.js and using hapi framework. and for the additional function we also make api to get mitra & seller location. After that, we deploy the api to get the endpoint to Compute Engine in GCP.
