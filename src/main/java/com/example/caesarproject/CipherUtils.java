package com.example.caesarproject;


import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.util.Arrays.asList;

public class CipherUtils {
    private static final List<Character> SYMBOLS = List.of('.', ',', '!', '?', '"', ':', '-', ' ');
    private static final Set<Character> VOWELS = new HashSet<>(asList('а', 'е', 'ё', 'и', 'й', 'о', 'у', 'ы', 'ю', 'я'));

    private static int findSubstringInText(String text, String subString) {
        int pointsCount = 0;
        int indexSubstring = text.indexOf(subString);
        while (indexSubstring != -1) {
            pointsCount += 5;
            indexSubstring = text.indexOf(subString, indexSubstring + 1);
        }
        return pointsCount;
    }

    private static int isNeverCoincidence(String str) {
        int count = 0;
        for (int j = 0; j < str.length() - 5; j++) {
            char c1 = str.charAt(j);
            char c2 = str.charAt(j + 1);
            char c3 = str.charAt(j + 2);
            char c4 = str.charAt(j + 3);
            char c5 = str.charAt(j + 4);
            if ((!VOWELS.contains(c1) && !SYMBOLS.contains(c1)) && (!VOWELS.contains(c2) && !SYMBOLS.contains(c2)) && (!VOWELS.contains(c3) && !SYMBOLS.contains(c3)) && (!VOWELS.contains(c4) && !SYMBOLS.contains(c4)) && (!VOWELS.contains(c5) && !SYMBOLS.contains(c5))) {
                count += 20;
            }
        }
        return count;
    }

    private static Path createNewPath(Path oldPath, int keyCipher, String select) {

        int count = 0;
        int endIndex = oldPath.toString().lastIndexOf(".");
        String nameFile = oldPath.toString().substring(0, endIndex);

        String fileExtension = oldPath.toString().substring(endIndex);
        String tempPath = nameFile + "__key_" + keyCipher + select + fileExtension;
        while (Files.exists(Path.of(tempPath))) {
            tempPath = reName(oldPath.toString(), fileExtension, ++count, keyCipher);
        }


        return Path.of(tempPath);
    }

    public static String reName(String path, String extension, int count, int key) {
        return path.substring(0, path.lastIndexOf(".")) + "__key_" + key + "_cipheredText (" + count + ")" + extension;
    }

    private static char replacementSymbolAndWord(char word, int key) {
        final int ALPHABET_SIZE = 32;

        if (word >= 'А' && word <= 'Я') {
            word += key % ALPHABET_SIZE;
            if (word < 'А') {
                word += ALPHABET_SIZE;
            }
            if (word > 'Я') {
                word -= ALPHABET_SIZE;
            }
        } else if (word >= 'а' && word <= 'я') {
            word += key % ALPHABET_SIZE;
            if (word < 'а') {
                word += ALPHABET_SIZE;
            }
            if (word > 'я') {
                word -= ALPHABET_SIZE;
            }
        } else {
            word = SYMBOLS.get((SYMBOLS.indexOf(word) + key) % SYMBOLS.size());
        }
        return word;
    }

    private static boolean isCheckValidChar(char[] array) throws BusinessException.CharValidException {
        for (int i = 0; i < array.length; i++) {
            char symbol = array[i];
            if (!(symbol >= 1040 && symbol <= 1103 || SYMBOLS.contains(symbol))) {
                throw new BusinessException.CharValidException(String.format("Недопустимый символ для кодировки \"%s\", позиция символа: %d", symbol, i));

            }
        }

        return true;
    }

    private static Map<Integer, String> getAllRot(String text) {
        Map<Integer, String> result = new HashMap<>();

        for (int i = 0; i <= 31; i++) {
            String rot = codingText(text, i);
            result.put(i, rot);
        }

        return result;
    }

    public static String getTextFromFile(Path path) throws BusinessException.MyFileEmpty, BusinessException.MyFileNotFoundException {
        String text;
        try (FileChannel inChannel = FileChannel.open(path)) {
            ByteBuffer buffer = ByteBuffer.allocate((int) inChannel.size());
            inChannel.read(buffer);
            buffer.flip();
            text = new String(buffer.array(), StandardCharsets.UTF_8);
            if (text.length() == 0) {
                throw new BusinessException.MyFileEmpty("Загруженный файл не имеет текста!");
            }
        } catch (IOException e) {
            throw new BusinessException.MyFileNotFoundException("По указанному пути " + "\"" + path + "\"" + " файл не был найден");
        }
        return text;
    }

//    public static int getKey(Scanner scanner) {
//        System.out.println("Укажите ключ:");
//        int key = 0;
//        if (scanner.hasNextInt()) {
//            key = Integer.parseInt(scanner.nextLine());
//            while (key < 0) {
//                System.out.println("Ключ не может быть отрицательным!");
//                key = Integer.parseInt(scanner.nextLine());
//            }
//        }
//
//        return key % 32;
//    }

    public static String codingText(String text, int key) throws BusinessException.CharValidException{
        char[] charArray = text.toCharArray();
        if (isCheckValidChar(charArray)) {
            for (int i = 0; i < charArray.length; i++) {
                char c = charArray[i];
                charArray[i] = replacementSymbolAndWord(c, key);
            }
        }
        return new String(charArray);
    }

    public static String unCodingText(String text, int key) throws BusinessException.CharValidException {

        return codingText(text, 32 - (key % 26));
    }

    public static String getBruteforce(String textFromFile) {
        Map<Integer, String> allRot = CipherUtils.getAllRot(textFromFile);
        StringBuilder resultString = new StringBuilder();
        int max = 0;
        for (int i = 0; i < allRot.size(); i++) {
            int count = 0;

            String rot = allRot.get(i);
            for (int j = 0; j < rot.length() - 1; j++) {
                char c = rot.charAt(j);
                if (Character.isSpaceChar(c)) {
                    count++;
                }
                if (Character.isSpaceChar(c) && Character.isUpperCase(rot.charAt(j + 1))) {
                    count++;
                }
            }

            count += findSubstringInText(rot, ". ");
            count += findSubstringInText(rot, ", ");
            count += findSubstringInText(rot, "? ");
            count += findSubstringInText(rot, "! ");
            count += findSubstringInText(rot, " - ");

            count -= isNeverCoincidence(rot);

            if (count > max) {
                max = count;
                resultString.delete(0, resultString.length());
                resultString.append(allRot.get(i));
            }
        }

        return resultString.toString();
    }

    public static void recordingInFile(Path oldPath, String textForRecording, int keyCipher, String select) {
        Path newPath = createNewPath(oldPath, keyCipher, select);

        try (RandomAccessFile raf = new RandomAccessFile(String.valueOf(Files.createFile(newPath)), "rw");
             FileChannel out = raf.getChannel()) {
            ByteBuffer buffer = ByteBuffer.wrap(textForRecording.getBytes(StandardCharsets.UTF_8));
            out.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}