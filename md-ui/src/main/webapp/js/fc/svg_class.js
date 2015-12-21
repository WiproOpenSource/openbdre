/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

//used to remove a particular class from a jquery selection
var removeClassSVG = function(elem, classToRemove) {

    // return all the classes of matched elements
    var classes = elem.attr('class');

    //if no classes on the concerned elements are found, return false.
    if (!classes) 	return false;

    // index will be the position of the class to be removed in the classes string
    var index = classes.search(classToRemove);
    	
    // if no match is found return false
    if (index == -1) return false;

    else {
        // classes_prefix contains all matched classes upto the matched class (excluding it)
        classes_prefix=classes.substring(0, index);
        // classes_prefix contains all matched classes from the matched class till the end of classes string (excluding it)
        classes_suffix=classes.substring((index + classToRemove.length), classes.length);

        classes = classes_prefix + classes_suffix;
    	
        // Newly constructed string will be the new classes attribute
        elem.attr('class', classes);

        return true;
    }
};

// check if a jquery selection has a given class
var hasClassSVG = function(elem, classToCheck) {
    var classes = elem.attr('class');
    if (!classes)  return false;

    // index will be the position of the class if found in the classes string
    var index = classes.search(classToCheck);

    // if no matching class is found return false
    if (index == -1) return false;
    // if a matching class is found return true
    else  return true;
};