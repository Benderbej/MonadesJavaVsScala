package com.company;

/**
 * ******************************************************************************************************************************
 * What is the COMPOSITION of a function?
 *
 * It performs a set of operations on an input in order to produce an output.
 * Therefore, a composition of functions occurs when the output, or result, of one function becomes the input of another function.
 * For functions represented by f(x) or g(x), the composition would be represented by f(g(x)) or g(f(x)).
 *
 * ******************************************************************************************************************************
 * What is IDENTITY function with example?
 *
 * The function f is called the identity function if each element of set A has an image on itself i.e. f (a) = a ∀ a ∈ A. It is denoted by I.
 * Example: Consider, A = {1, 2, 3, 4, 5} and f: A → A such that. f = {(1, 1), (2, 2), (3, 3), (4, 4), (5, 5)}.
 *
 *
 *
 *
 */


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class Main {

    //типичная java-функция потенциально может вернуть null
    static Integer canReturnNull(Integer i){
        if(i < 5){
            return null;
        } else {
            return i;
        }
    }

    static Function<Integer, Optional<Integer>> canReturnNullButFilteredWithNullable = x -> {
        if (x == null) {
            x = -1;
        } else if (x == 2) {
            x = null;
        } else {
            x = x + 1;
        }
        return Optional.ofNullable(x);
    };

    public static void main(String[] args) {

//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//Optional нас сам по себе от null-а не страхует

//        Optional<Integer> nf1 = Optional.of(canReturnNull(7));
//        System.out.println(nf1);
//        Optional<Integer> nf2 = Optional.of(canReturnNull(3));
//        System.out.println(nf2);


// ЛОМАЕМ ЗАКОН COMPOSITION для map()
//COMPOSITION - один из законов, действующий на монады
//        https://www.deadcoderising.com/2015-09-07-java-8-functional-composition-using-compose-and-andthen/
//        https://www.sitepoint.com/how-optional-breaks-the-monad-laws-and-why-it-matters/
/*
        ЗАКОН АСССОЦИАТИВНОСТИ(ИЛИ COMPOSITION)
        Associativity, if we have a chain of monadic function applications,
        it doesn’t matter how they are nested:

        bind(bind(m, f), g) === bind(m, x -> g(f(x))).

*/

        //функции участвующие в маппинге
        Function<Integer, Integer> f = x -> x % 10; //функция f возвращает остаток от деления на 10
        Function<Integer, Integer > g = y -> canReturnNull(y);// какая нибудь функция участвующая в цепочке маппинга может смело кинуть нам null

        Optional<Integer> opt = Optional.of(7);  // может повезет
//        Optional<Integer> opt = Optional.of(2);  // а может не повезет

        //В зависимости от порядка выполнения мы можем выхватить nullPointerException
        String s2 = opt.map(f.andThen(g)).toString();//где то все отработало как надо
        System.out.println(s2);
        String s1 = opt.map(f.compose(g)).toString();//а где то вылетело исключение (andThen и compose меняют порядок выполнения)
        System.out.println(s1);
        //Если optional вернет null а он может вернуть null в любом случае





	// ЛОМАЕМ ЗАКОН IDENTITY для flatMap()
    // LEFT IDENTITY
        /*
        Left identity, applying the unit function to a value and then binding the resulting monad to function f
         is the same as calling f on the same value:
         let f be a function returning a monad, then bind(unit(value), f) === f(value).
        */


        //мы ожидаем что Optional от какого-то значения с примененной к нему функцией flatMap(от функции) будет всегда равен функции с вызванным значением
        //ведь теория о монадах(а именно left identity law) гласит:
        //если к некоторой единичной функции   применить значение (1 или 2 или null) и связать результирующую монаду с функцией canReturnNullButFilteredWithNullable
        //это будет то же самое, что и вызов функции canReturnNullButFilteredWithNullable с этим параметром (1 или 2 или null)
        Boolean b1 = Optional.of(1).flatMap(canReturnNullButFilteredWithNullable).equals(canReturnNullButFilteredWithNullable.apply(1));// true, Optional[2] === Optional[2]
        System.out.println(b1);
        Boolean b2 = Optional.of(2).flatMap(canReturnNullButFilteredWithNullable).equals(canReturnNullButFilteredWithNullable.apply(2));// true, Optional.empty === Optional.empty
        System.out.println(b2);
        Boolean b3 = Optional.ofNullable((Integer) null).flatMap(canReturnNullButFilteredWithNullable).equals(canReturnNullButFilteredWithNullable.apply(null));// false -
        System.out.println(b3);
        //Но выходит что есть различия
        System.out.println(Optional.ofNullable((Integer) null).flatMap(canReturnNullButFilteredWithNullable));     // prints "Optional.empty"
        System.out.println(canReturnNullButFilteredWithNullable.apply(null));                                   // prints "Optional[-1]" что и ожидали от функции


        //При этом, помним, что flatMap используется для работы со стримами!
//        https://vertex-academy.com/tutorials/ru/java-8-stream-flatmap/?doing_wp_cron=1581002847.1219990253448486328125


    }




}


/**
 * package monads/**
 *  из вики:
 * Монада является контейнером, который хранит в себе значение произвольного типа.
 * Она должна обладать функцией связывания (bind), которая принимает два аргумента:
 * текущее значение монады и
 * функцию, принимающую значение типа, который содержит текущая монада и возвращающая новую монаду.
 * Результатом вызова функции связывания будет новая монада, полученная путём применения первого аргумента ко второму.
 * Так могла бы выглядеть монада в императивном языке Java и одна из её реализаций, контейнер Maybe:  */


/**
 *
 * по определению монады можно написать следующий класс
 *
 class MyMonade {
    def bind[T](acc: T)(f: T => MyMonade): MyMonade = {
        f(acc)
    }
 //  по сигнатуре ближе всего для Option - flatMap()
 но в нем исп this

    *@inline final def flatMap[B](f:A=>Option[B]):Option[B]=
        if(isEmpty)None else f(this.get)}

    в jav-овском flatMape-вклинена проверка на Null,что уже каноническое определение монады ломает

    public<U> Optional<U> flatMap(Function<? super T,Optional<U>>var1){
        Objects.requireNonNull(var1);
        return!this.isPresent()?empty():(Optional)Objects.requireNonNull(var1.apply(this.value));
    }
 */
