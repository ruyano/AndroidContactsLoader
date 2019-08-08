# AndroidContactsLoader [![Build Status](https://api.travis-ci.com/ruyano/AndroidContactsLoader.svg?branch=master)](https://travis-ci.com/ruyano/AndroidContactsLoader)
AndroidContactsLoader is a fast and efficient contacts loading libary for Android focused on performance. Its main point is the option to get the result in a paginated form, so you can load large amounts of contacs almost immediately

## Installation
#### 1 - Add JitPack to the repositores
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
#### 2 - Add the dependency:
```
implementation 'implementation 'com.github.ruyano:AndroidContactsLoader:0.1.0''
```
## Requirements

#### This lib requires the read contacts permission:
```
<uses-permission android:name="android.permission.READ_CONTACTS"/>
```
#### [RXAndroid](https://github.com/ReactiveX/RxAndroid)
Every method inside the lib returns Observable objects, so you'll need to use RXAndroid to get the responses. It's strongly recomended that you use "Schedulers.newThread()" so the process runs in a background thread.

## How to use
##### Ther are 4 ways to load the contacts:  
  1 - All contacts by once  
  2 - Query contacts by name (This will returns all contacts with some part of the name equal the name parameter)  
  3 - All contacts paginated (Use along paginated RacyclerVIew or ListView)  
  4 - Query contacts by name paginated (Use along paginated RacyclerVIew or ListView) 
##### 1 - All contacts by once
```
CompositeDisposable().add(ContactsLoader(this).load()
            .subscribeOn(Schedulers.newThread())
            .doOnSubscribe { doOnSubscribe() }
            .subscribe { contacts -> doOnFinish(contacts) })
```
##### 2 - Query contacts by name  
```
CompositeDisposable().add(ContactsLoader(this).load("name")
            .subscribeOn(Schedulers.newThread())
            .doOnSubscribe { doOnSubscribe() }
            .subscribe { contacts -> doOnFinish(contacts) })
```
##### 3 - All contacts paginated
```
CompositeDisposable().add(ContactsLoader(this).load(pageNumber, pageSize)
            .subscribeOn(Schedulers.newThread())
            .doOnSubscribe { doOnSubscribe() }
            .subscribe { contacts -> doOnFinish(contacts) })
```
##### 4 - Query contacts by name paginated
```
CompositeDisposable().add(ContactsLoader(this).load(name, pageNumber, pageSize)
            .subscribeOn(Schedulers.newThread())
            .doOnSubscribe { doOnSubscribe() }
            .subscribe { contacts -> doOnFinish(contacts) })
```

## License
```
MIT License

Copyright (c) 2019 Ruyano

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
