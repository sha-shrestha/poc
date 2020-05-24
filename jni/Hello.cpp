#include "Hello.h"
#include <iostream>
#include <chrono>
#include <ctime>
#include <thread>

/*
 * Class:     com_baeldung_jni_HelloWorldJNI
 * Method:    sayHello
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_Hello_sayHello(JNIEnv *env, jobject thisObject)
{
    std::string hello = "Hello from C++";
    std::cout << hello << std::endl;

    std::size_t counter = 0U;

    while (true)
    {
        std::time_t current_time = std::chrono::system_clock::to_time_t(std::chrono::system_clock::now());
        std::cout << "Iteration # " << ++counter << " Current time " << std::ctime(&current_time);
        std::cout << "Will sleep for 5 seconds " << std::endl;
        std::this_thread::sleep_for(std::chrono::milliseconds(5000));
    }

    return env->NewStringUTF(hello.c_str());
}