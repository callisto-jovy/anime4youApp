/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 29.12.20, 17:45
 */

package net.bplaced.abzzezz.animeapp.util.provider.holders;

import java.util.Random;

public interface Anime4YouHolder {

    String CAPTCHA_ANIME_4_YOU_ONE = "https://captcha.anime4you.one";
    String REQUEST_URL = "http://abzzezz.bplaced.net/app/request.php";
    String DATABASE = "https://www.anime4you.one/speedlist.old.txt";
    String BACKUP_DATABASE = "http://abzzezz.bplaced.net/list.txt";
    String COVER_API = "https://cdn.anime4you.one/covers/";

    default String generateRandomKey() {
        final String[] keys = {"2fe519479de544f68a46f5284a9e94dac6bc36f0fcee744312f92a77616bb790e705f8b0c03c582d",
                "c19efb70201627778f8a3aa678d61b2c482503e75d470fc3357a837011e9a2c2b2a9c9815f854501",
                "7291ce814dae8d8948f7ff07e994ecb5d8147364d9ebce3c",
                " 456b2436b2eeecadb765fd07767a977c914eb59059a3e888",
                "   9fee148ac2b373c4ab7508cd587293287957906a5c762101",
                "  c9f020dbfb22002897ed56b4c8c9d69392c5463170da754c",
                "  67a34213943bb7f48fca8b8b626b6332f0046cdeeaefe51e",
                " a91ff6bcfd7053c1c38d620b3e6f68236fb3f6fd38aa2fbb",
                "  fc018919a103eaa13b7607c10d592e22ec34035d3af2b833",
                "     1409a3b8cabd2d7156ff5e127427bf3c794a406d7d1da5b2",
                "    423ec9069280f375dd169e1b39564c3e826ff3181f6c907d",
                "   54f072c8375d9671e40b1944258c14e4e43a23b34edaefa7",
                " 9279f337c793d716f1161e526705f8477a29bcda9a97da51",
                " 5c599f8defdaf2529f0c9e9931f0baf0bd684aea64d8a163"};
        return keys[new Random().nextInt(keys.length)];
    }
}
