// IUserInterface.aidl
package me.wcy.app;

// Declare any non-default types here with import statements

interface IUserInterface {
    long getUserId();

    String getUserName(long userId);
}
