package com.example.data.quotes

data class Quote(
    val id: Int,
    val bangla: String,
    val english: String,
    val author: String = "SSC 27 Motivation"
)

object MotivationalQuotes {
    val quotes = listOf(
        Quote(
            id = 1,
            bangla = "পরিশ্রম কখনো বৃথা যায় না — তোমার আজকের প্রতিটি ঘণ্টা আগামীকালের সোনালী ফলাফল।",
            english = "Hard work never goes in vain — every focused hour today crafts tomorrow's success.",
            author = "SSC 27 Guidance"
        ),
        Quote(
            id = 2,
            bangla = "লক্ষ্য স্থির রাখো, সফলতা আসবেই। SSC 2027 তোমার জন্য ইতিহাস গড়ার এক অনন্য সুযোগ!",
            english = "Keep your goal steadfast. SSC 2027 is your historic moment to shine.",
            author = "Dr. APJ Abdul Kalam"
        ),
        Quote(
            id = 3,
            bangla = "দিনের হিসাব গুনে লাভ নেই, প্রতিটি দিনকে স্মরণীয় ও ফলপ্রসূ করে তোলো।",
            english = "Don't count the days, make every single day count.",
            author = "Muhammad Ali"
        ),
        Quote(
            id = 4,
            bangla = "সময়ের সঠিক মূল্যায়ন যে করে, বিজয় তার পদচুম্বন করবেই। এক মুহূর্তও অবহেলায় হারিও না।",
            english = "He who values time conquers the world. Do not lose even a second in hesitation.",
            author = "Albert Einstein"
        ),
        Quote(
            id = 5,
            bangla = "আজকের ত্যাগ ও ক্লান্তিই আগামীকালের শ্রেষ্ঠ গৌরব ও গর্বের চাবিকাঠি।",
            english = "Today's sacrifice and sweat are the golden keys to tomorrow's highest triumph.",
            author = "Kazi Nazrul Islam"
        ),
        Quote(
            id = 6,
            bangla = "সফলতা কোনো আকস্মিক ঘটনা নয়; এটি নিয়মনিষ্ঠ অনুশীলন, গভীর ধৈর্য এবং অবিচল অধ্যবসায়ের ফল।",
            english = "Success is no accident; it is the fruit of deliberate practice, patience, and sheer willpower.",
            author = "Pelé"
        ),
        Quote(
            id = 7,
            bangla = "তোমার মেধা এবং প্রচেষ্টাই তোমার ভবিষ্যৎ নির্ধারণ করবে। নিজের শক্তির ওপর অটুট বিশ্বাস রাখো।",
            english = "Your discipline and consistent effort will define your future. Believe in your boundless power.",
            author = "Rabindranath Tagore"
        ),
        Quote(
            id = 8,
            bangla = "বড় স্বপ্ন দেখো, একাগ্রচিত্তে পড়াশোনা করো। নিয়মানুবর্তিতার কষ্ট অনুশোচনার কষ্টের চেয়ে অনেক হালকা।",
            english = "Dream big, study relentlessly. The ache of discipline weighs ounces; regret weighs tons.",
            author = "Jim Rohn"
        ),
        Quote(
            id = 9,
            bangla = "প্রতিটি অধ্যায় শেষ করা মানে তোমার স্বপ্নের আরও এক ধাপ কাছে পৌঁছানো। থামবে না!",
            english = "Mastering each chapter brings you one giant leap closer to your summit. Never stop!",
            author = "SSC 27 Mentorship"
        ),
        Quote(
            id = 10,
            bangla = "যে ভোর তোমার চোখে পড়ার আলো জ্বালায়, সে ভবিষ্যৎ তোমাকে সম্মানের মুকুট পরাবে।",
            english = "The early dawn that illuminates your study desk will crown your tomorrow with victory.",
            author = "Inspirational Wisdom"
        ),
        Quote(
            id = 11,
            bangla = "কঠিন পথই সবসময় সবচেয়ে সুন্দর এবং মহৎ গন্তব্যে নিয়ে যায়।",
            english = "Difficult roads often lead to the most breathtaking destinations.",
            author = "Life Lessons"
        ),
        Quote(
            id = 12,
            bangla = "পড়াশোনাকে বোঝা ভাবলে চলবে না, এটি তোমার নিজের সম্ভাবনাকে উন্মোচনের মহাসুযোগ।",
            english = "Never view study as a burden, but as an enviable opportunity to unlock your infinite potential.",
            author = "Albert Einstein"
        ),
        Quote(
            id = 13,
            bangla = "আজ ঘুমানোর আগে নিজেকে জিজ্ঞেস করো: আমি কি SSC 27-এর জন্য আমার শতভাগ দিয়েছি?",
            english = "Before you sleep tonight, ask yourself: Did I give my absolute best for SSC 27 today?",
            author = "Daily Reflection"
        ),
        Quote(
            id = 14,
            bangla = "ধারাবাহিকতাই সফলতার আসল রহস্য। রোজ অল্প একটু এগিয়ে চলাই অনেক দূর নিয়ে যায়।",
            english = "Consistency is the secret code. Small daily progress compound into massive victory.",
            author = "John Maxwell"
        )
    )

    fun getDailyQuote(dayOfYear: Int = java.time.LocalDate.now().dayOfYear): Quote {
        val index = (dayOfYear % quotes.size).coerceIn(0, quotes.size - 1)
        return quotes[index]
    }

    fun getRandomQuote(excludeId: Int = -1): Quote {
        val filtered = quotes.filter { it.id != excludeId }
        return filtered.randomOrNull() ?: quotes.first()
    }
}
