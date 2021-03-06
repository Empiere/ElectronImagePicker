# :fire: Electron Image Picker Library :fire:

[![](https://jitpack.io/v/Empiere/ElectronImagePicker.svg)](https://jitpack.io/#Empiere/ElectronImagePicker)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# Demo :fire:
![](https://github.com/Empiere/ElectronImagePicker/blob/main/readme_data/Image_Picker_Banner.gif)

# Setup :key:
**1. Add this to your project level build.gradle file**

``` kotlin
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
**1. Add this to your project level settings.gradle file [For Newer version]**
``` kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**2. Add this to your module level build.gradle file**

``` kotlin
dependencies {
    implementation 'com.github.Empiere:ElectronImagePicker:1.0'
}
```
<br></br>
# How to use:question:

` Make sure app have READ_EXTERNAL_STORAGE permission before starting below activity.`

> ## Start ElectronImagePickerActivity::class.java from your activity or fragment

``` kotlin
val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                result.data?.let { intent ->

                    val imageDataInJson = intent.getStringExtra(ELECTRON_IMAGES_DATA)
                    val message = intent.getStringExtra(MESSAGE)

                    Log.i(TAG, "RESULT_OK: Message - $message")

                    imageDataInJson?.let { json ->
                        val listOfElectronImage = getElectronImageListFromData(json) // Library function
                        listOfElectronImage.forEach {
                            Log.i(TAG, "Data : $it")
                        }
                    }

                }

            } else if (result.resultCode == Activity.RESULT_CANCELED) {

                result.data?.let {
                    Log.i(TAG, "RESULT_CANCELED: Message - " + it.getStringExtra(MESSAGE))
                }

            }

        }
```
<br></br>
> ## For Single Image Picker
``` kotlin
val intent = Intent(this, ElectronImagePickerActivity::class.java)
intent.putExtra(SELECTION_TYPE, SelectionType.SINGLE)
intent.putExtra(MIN_IMAGE_SIZE_IN_BYTE, getBytesFromMB(0f))
intent.putExtra(MAX_IMAGE_SIZE_IN_BYTE, getBytesFromMB(5f))
imageLauncher.launch(intent)
```
``` 
Output :
I/MainActivity: RESULT_OK: Message - User selected 1 images.
I/MainActivity: Data : ElectronImage(id=32, name=cute_dog_604243.jpg, sizeInBytes=2135089, formattedSize=2.14 MB, contentUri=content://media/external/images/media/32, filePath=/storage/emulated/0/Pictures/Picture_3.jpg)
```
<br></br>
> ## For Multiple Image Picker
``` kotlin
val intent = Intent(this, ElectronImagePickerActivity::class.java)
intent.putExtra(SELECTION_TYPE, SelectionType.MULTIPLE)
intent.putExtra(MAX_MULTI_SELECTION_SIZE, MaxMultiSelectionSize.FIVE) // Between [MaxMultiSelectionSize.ONE to MaxMultiSelectionSize.TEN]
intent.putExtra(MIN_IMAGE_SIZE_IN_BYTE, getBytesFromMB(0f))
intent.putExtra(MAX_IMAGE_SIZE_IN_BYTE, getBytesFromMB(5f))
imageLauncher.launch(intent)
```
``` 
Output :
I/MainActivity: RESULT_OK: Message - User selected 5 images.
I/MainActivity: Data : ElectronImage(id=76, name=Camera_1.jpg, sizeInBytes=200987, formattedSize=201 kB, contentUri=content://media/external/images/media/76, filePath=/storage/emulated/0/Download/Clicked/Camera_1.jpg)
I/MainActivity: Data : ElectronImage(id=48, name=Download_1.jpg, sizeInBytes=329551, formattedSize=330 kB, contentUri=content://media/external/images/media/48, filePath=/storage/emulated/0/Pictures/empieretech/Download_1.jpg)
I/MainActivity: Data : ElectronImage(id=32, name=cute_dog_604243.jpg, sizeInBytes=2135089, formattedSize=2.14 MB, contentUri=content://media/external/images/media/32, filePath=/storage/emulated/0/Pictures/Picture_3.jpg)
I/MainActivity: Data : ElectronImage(id=24, name=adorable_animal_breed_canine_cute_dog_doggy_603425.webp, sizeInBytes=45206, formattedSize=45.21 kB, contentUri=content://media/external/images/media/24, filePath=/storage/emulated/0/Pictures/Pic Folder/Pic_2.webp)
I/MainActivity: Data : ElectronImage(id=20, name=IMG_20220510_115825.jpg, sizeInBytes=144058, formattedSize=144 kB, contentUri=content://media/external/images/media/20, filePath=/storage/emulated/0/DCIM/Camera/Camera_3.jpg)
```
<br></br>
# Documentation :page_facing_up:

## Required Intent data for starting ElectronImagePickerActivity

> :one: SELECTION_TYPE
``` kotlin
intent.putExtra(SELECTION_TYPE, SelectionType.SINGLE)
or
intent.putExtra(SELECTION_TYPE, SelectionType.MULTIPLE)
```

> :two: MAX_MULTI_SELECTION_SIZE
`(Only if you passed SelectionType.MULTIPLE in SELECTION_TYPE)`
``` kotlin
intent.putExtra(MAX_MULTI_SELECTION_SIZE, MaxMultiSelectionSize.FIVE) 
/*
	Default value is MaxMultiSelectionSize.TWO
	Range between [MaxMultiSelectionSize.ONE to MaxMultiSelectionSize.TEN]
*/
```
> :three: MIN_IMAGE_SIZE_IN_BYTE
``` kotlin
intent.putExtra(MIN_IMAGE_SIZE_IN_BYTE, getBytesFromMB(0f)) 
/*
	Default value is getBytesFromKB(1f)
	
	Predefined functions from library :
		fun getBytesFromMB(mb: Float): String // Returns bytes in string
   	        fun getBytesFromKB(kb: Float): String // Returns bytes in string
*/
```
> :four: MAX_IMAGE_SIZE_IN_BYTE
``` kotlin
intent.putExtra(MAX_IMAGE_SIZE_IN_BYTE, getBytesFromMB(3f)) 
/*
	Default value is getBytesFromMB(5f)
	
	Predefined functions from library :
		fun getBytesFromMB(mb: Float): String // Returns bytes in string
   	        fun getBytesFromKB(kb: Float): String // Returns bytes in string
*/
```
<br></br>
## How to get ElectronImagePickerActivity result data:question:

> :one: MESSAGE
``` kotlin
val message = intent.getStringExtra(MESSAGE)
/*
	Returns appropriate Success or Error message.
*/
```

> :two: ELECTRON_IMAGES_DATA
``` kotlin
val imageDataInJson = intent.getStringExtra(ELECTRON_IMAGES_DATA)
/*
	Returns list of ElectronImage object in Json format.
*/
```

` How to Get list of ElectronImage Object from Json data?`
``` kotlin
val listOfElectronImage = getElectronImageListFromData(json) 
/*
	Predefined functions from library :
	getElectronImageListFromData(json: String): List<ElectronImage> 
	
	Returns list of ElectronImage object in Json format.
*/
```
<br></br>
## ElectronImage Data Class :wrench:

``` kotlin

data class ElectronImage(
    val id: Long,
    val name: String,
    val sizeInBytes: Long,
    val formattedSize: String,
    val contentUri: String,
    val filePath: String
)

```

# Thank you for using our library :pray:
