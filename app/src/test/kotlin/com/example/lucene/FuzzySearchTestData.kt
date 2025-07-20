package com.example.lucene

import java.util.stream.Stream
import org.junit.jupiter.params.provider.Arguments

object FuzzySearchTestData {

    val couponsForFuzzySearch: Collection<Coupon> =
        listOf(
            Coupon(token = "token1", description = "available for female fashions and clothes"),
            Coupon(token = "token2", description = "available for male tools and accessories"),
            Coupon(
                token = "C001",
                description =
                    "Get 50% off on all women's fashion and elegant accessories for spring collection.",
            ),
            Coupon(
                token = "C002",
                description =
                    "Special discount for men's tools, durable gadgets, and DIY equipment.",
            ),
            Coupon(
                token = "C003",
                description =
                    "Limited time offer: free shipping on books, novels, and educational materials.",
            ),
            Coupon(
                token = "C004",
                description =
                    "Huge savings on electronics, including smartphones, laptops, and smart home devices.",
            ),
            Coupon(
                token = "C005",
                description =
                    "Delicious food items: organic vegetables, fresh fruits, and gourmet groceries.",
            ),
            Coupon(
                token = "C006",
                description =
                    "Travel deals: discounts on flights, hotels, and vacation packages worldwide.",
            ),
            Coupon(
                token = "C007",
                description =
                    "Health and wellness: vitamins, supplements, and fitness equipment for a better life.",
            ),
            Coupon(
                token = "C008",
                description =
                    "Home decor: stylish furniture, unique art pieces, and cozy bedding for your living space.",
            ),
            Coupon(
                token = "C009",
                description =
                    "Kids toys: educational games, building blocks, and fun outdoor play items.",
            ),
            Coupon(
                token = "C010",
                description =
                    "Pet supplies: premium dog food, cat toys, and accessories for your beloved companions.",
            ),
            Coupon(
                token = "C011",
                description =
                    "Sports gear: athletic wear, running shoes, and outdoor adventure equipment.",
            ),
            Coupon(
                token = "C012",
                description =
                    "Automotive parts: engine oils, car accessories, and maintenance tools for vehicles.",
            ),
            Coupon(
                token = "C013",
                description =
                    "Art and craft supplies: paints, brushes, canvases, and creative kits for hobbies.",
            ),
            Coupon(
                token = "C014",
                description =
                    "Musical instruments: guitars, keyboards, drums, and sheet music for aspiring musicians.",
            ),
            Coupon(
                token = "C015",
                description =
                    "Gardening tools: shovels, seeds, plants, and outdoor decor for your beautiful garden.",
            ),
            Coupon(
                token = "C016",
                description =
                    "Jewelry and watches: elegant rings, sparkling necklaces, and luxury timepieces.",
            ),
            Coupon(
                token = "C017",
                description =
                    "Beauty products: skincare, makeup, fragrances, and hair care essentials.",
            ),
            Coupon(
                token = "C018",
                description =
                    "Office supplies: pens, paper, printers, and ergonomic chairs for productive work.",
            ),
            Coupon(
                token = "C019",
                description =
                    "Cleaning supplies: eco-friendly detergents, vacuums, and brushes for a spotless home.",
            ),
            Coupon(
                token = "C020",
                description =
                    "Party supplies: balloons, decorations, costumes, and snacks for memorable events.",
            ),
            Coupon(
                token = "C021",
                description =
                    "Winter clothes: warm coats, cozy sweaters, and sturdy boots for cold weather.",
            ),
            Coupon(
                token = "C022",
                description =
                    "Summer wear: light dresses, swimming suits, and sandals for sunny days.",
            ),
            Coupon(
                token = "C023",
                description =
                    "Tech gadgets: drones, smartwatches, and virtual reality headsets for enthusiasts.",
            ),
            Coupon(
                token = "C024",
                description =
                    "Bakery items: fresh bread, delicious cakes, and pastries for delightful treats.",
            ),
            Coupon(
                token = "C025",
                description =
                    "Home appliances: blenders, toasters, coffee makers, and kitchen essentials.",
            ),
            Coupon(
                token = "C026",
                description =
                    "DIY kits: craft materials, repair tools, and project guides for creative minds.",
            ),
            Coupon(
                token = "C027",
                description =
                    "Vintage items: antique furniture, retro electronics, and classic collectibles.",
            ),
            Coupon(
                token = "C028",
                description =
                    "Gaming consoles: latest playstations, xbox, and nintendo for avid gamers.",
            ),
            Coupon(
                token = "C029",
                description =
                    "Fitness wear: yoga pants, compression shirts, and sports bras for active lifestyle.",
            ),
            Coupon(
                token = "C030",
                description =
                    "Outdoor camping gear: tents, sleeping bags, and portable cooking equipment.",
            ),
            Coupon(
                token = "C031",
                description =
                    "Baby essentials: diapers, strollers, car seats, and baby food for new parents.",
            ),
            Coupon(
                token = "C032",
                description =
                    "Art prints: limited edition posters, framed artwork, and decorative wall hangings.",
            ),
            Coupon(
                token = "C033",
                description =
                    "Coffee beans: single origin, dark roast, and decaf options for coffee lovers.",
            ),
            Coupon(
                token = "C034",
                description =
                    "Tea varieties: green tea, black tea, herbal infusions, and teaware sets.",
            ),
            Coupon(
                token = "C035",
                description =
                    "Kitchen gadgets: blenders, food processors, air fryers, and smart cooking devices.",
            ),
            Coupon(
                token = "C036",
                description =
                    "Photography equipment: DSLR cameras, lenses, tripods, and studio lighting.",
            ),
            Coupon(
                token = "C037",
                description =
                    "Educational toys: STEM kits, science experiments, and coding games for kids.",
            ),
            Coupon(
                token = "C038",
                description =
                    "Self-care products: bath bombs, essential oils, face masks, and relaxation kits.",
            ),
            Coupon(
                token = "C039",
                description =
                    "Party decorations: balloons, banners, confetti, and tableware for celebrations.",
            ),
            Coupon(
                token = "C040",
                description =
                    "Gardening supplies: fertilizers, planters, gardening gloves, and watering cans.",
            ),
            Coupon(
                token = "C041",
                description =
                    "Books for all ages: fiction, non-fiction, children's books, and audiobooks.",
            ),
            Coupon(
                token = "C042",
                description =
                    "Musical instruments: pianos, violins, flutes, and percussion instruments.",
            ),
            Coupon(
                token = "C043",
                description =
                    "Fitness supplements: protein powder, pre-workouts, and vitamins for athletes.",
            ),
            Coupon(
                token = "C044",
                description =
                    "Home security: cameras, alarm systems, smart locks, and video doorbells.",
            ),
            Coupon(
                token = "C045",
                description =
                    "Sustainable products: reusable bags, bamboo utensils, and eco-friendly cleaning supplies.",
            ),
            Coupon(
                token = "C046",
                description =
                    "Travel accessories: luggage, travel pillows, adapters, and packing cubes.",
            ),
            Coupon(
                token = "C047",
                description =
                    "Pet grooming: shampoos, brushes, nail clippers, and pet-friendly perfumes.",
            ),
            Coupon(
                token = "C048",
                description = "Craft beer selection: IPAs, stouts, lagers, and artisanal brews.",
            ),
            Coupon(
                token = "C049",
                description =
                    "Wine collection: red, white, rosé, and sparkling wines from around the world.",
            ),
            Coupon(
                token = "C050",
                description =
                    "Baking ingredients: flours, sugars, chocolates, and baking mixes for delicious treats.",
            ),
            Coupon(
                token = "C051",
                description =
                    "Outdoor furniture: patio sets, loungers, umbrellas, and outdoor cushions.",
            ),
            Coupon(
                token = "C052",
                description =
                    "Artistic supplies: watercolors, acrylics, oil paints, and drawing pencils.",
            ),
            Coupon(
                token = "C053",
                description =
                    "Board games: strategy games, party games, and family-friendly board games.",
            ),
            Coupon(
                token = "C054",
                description =
                    "Puzzle games: jigsaw puzzles, brain teasers, and logic puzzles for all ages.",
            ),
            Coupon(
                token = "C055",
                description =
                    "Sports equipment: footballs, basketballs, tennis rackets, and golf clubs.",
            ),
            Coupon(
                token = "C056",
                description =
                    "Camping tents: lightweight, family-sized, and pop-up tents for outdoor adventures.",
            ),
            Coupon(
                token = "C057",
                description =
                    "Hiking boots: waterproof, durable, and comfortable footwear for trails.",
            ),
            Coupon(
                token = "C058",
                description =
                    "Backpacks: hiking, travel, school, and casual backpacks for daily use.",
            ),
            Coupon(
                token = "C059",
                description =
                    "Skincare routines: cleansers, toners, serums, moisturizers, and sunscreens.",
            ),
            Coupon(
                token = "C060",
                description =
                    "Hair styling tools: curling irons, flat irons, hair dryers, and brushes.",
            ),
            Coupon(
                token = "C061",
                description =
                    "Makeup sets: eyeshadow palettes, lipstick kits, brush sets, and foundation.",
            ),
            Coupon(
                token = "C062",
                description =
                    "Fragrances: perfumes, colognes, body mists, and essential oil diffusers.",
            ),
            Coupon(
                token = "C063",
                description =
                    "Home cleaning gadgets: robot vacuums, steam mops, and handheld cleaners.",
            ),
            Coupon(
                token = "C064",
                description =
                    "Office organization: desk organizers, file cabinets, storage boxes, and labels.",
            ),
            Coupon(
                token = "C065",
                description =
                    "Stationery: notebooks, pens, markers, highlighters, and sticky notes.",
            ),
            Coupon(
                token = "C066",
                description =
                    "Party costumes: halloween costumes, cosplay outfits, and themed party attire.",
            ),
            Coupon(
                token = "C067",
                description =
                    "Snack boxes: assorted chips, cookies, candies, and healthy snack options.",
            ),
            Coupon(
                token = "C068",
                description = "Beverages: soft drinks, juices, energy drinks, and sparkling water.",
            ),
            Coupon(
                token = "C069",
                description =
                    "Breakfast cereals: various flavors, healthy grains, and gluten-free options.",
            ),
            Coupon(
                token = "C070",
                description = "Dairy products: milk, cheese, yogurt, and butter from local farms.",
            ),
            Coupon(
                token = "C071",
                description =
                    "Meat and poultry: fresh chicken, beef, pork, and seafood for your meals.",
            ),
            Coupon(
                token = "C072",
                description =
                    "Frozen foods: pizzas, ice creams, frozen vegetables, and ready-to-eat meals.",
            ),
            Coupon(
                token = "C073",
                description = "Canned goods: soups, beans, fruits, and vegetables for quick meals.",
            ),
            Coupon(
                token = "C074",
                description =
                    "Spices and herbs: variety packs, organic options, and exotic flavors for cooking.",
            ),
            Coupon(
                token = "C075",
                description =
                    "Baking tools: mixing bowls, measuring cups, spatulas, and baking sheets.",
            ),
            Coupon(
                token = "C076",
                description =
                    "Cookware sets: non-stick pans, stainless steel pots, and cast iron skillets.",
            ),
            Coupon(
                token = "C077",
                description =
                    "Dinnerware: plates, bowls, cups, and cutlery sets for your dining table.",
            ),
            Coupon(
                token = "C078",
                description =
                    "Glassware: wine glasses, tumblers, mugs, and specialty cocktail glasses.",
            ),
            Coupon(
                token = "C079",
                description =
                    "Kitchen storage: food containers, pantry organizers, and spice racks.",
            ),
            Coupon(
                token = "C080",
                description = "Bakeware: cake pans, muffin tins, cookie cutters, and cooling racks.",
            ),
            Coupon(
                token = "C081",
                description =
                    "Small appliances: toasters, blenders, coffee makers, and electric kettles.",
            ),
            Coupon(
                token = "C082",
                description =
                    "Large appliances: refrigerators, ovens, washing machines, and dishwashers.",
            ),
            Coupon(
                token = "C083",
                description =
                    "Home security systems: cameras, alarms, motion sensors, and smart locks.",
            ),
            Coupon(
                token = "C084",
                description =
                    "Smart home devices: voice assistants, smart plugs, thermostats, and lighting.",
            ),
            Coupon(
                token = "C085",
                description =
                    "Networking equipment: routers, modems, Wi-Fi extenders, and Ethernet cables.",
            ),
            Coupon(
                token = "C086",
                description =
                    "Computer accessories: keyboards, mice, monitors, webcams, and headsets.",
            ),
            Coupon(
                token = "C087",
                description =
                    "Printers and scanners: inkjet, laser, all-in-one printers, and document scanners.",
            ),
            Coupon(
                token = "C088",
                description =
                    "Software: operating systems, office suites, antivirus, and creative design software.",
            ),
            Coupon(
                token = "C089",
                description =
                    "External storage: hard drives, SSDs, USB flash drives, and memory cards.",
            ),
            Coupon(
                token = "C090",
                description =
                    "Gaming accessories: gaming mice, mechanical keyboards, headsets, and controllers.",
            ),
            Coupon(
                token = "C091",
                description =
                    "Drones and accessories: quadcopters, spare batteries, propellers, and cases.",
            ),
            Coupon(
                token = "C092",
                description =
                    "Virtual reality headsets: VR systems, controllers, and immersive games.",
            ),
            Coupon(
                token = "C093",
                description = "E-readers: Kindle, Kobo, and other e-ink devices for digital books.",
            ),
            Coupon(
                token = "C094",
                description =
                    "Tablets: iPads, Android tablets, and Windows tablets for work and play.",
            ),
            Coupon(
                token = "C095",
                description =
                    "Smartphones: latest models, unlocked phones, and accessories like cases and chargers.",
            ),
            Coupon(
                token = "C096",
                description =
                    "Wearable tech: smartwatches, fitness trackers, and health monitoring devices.",
            ),
            Coupon(
                token = "C097",
                description =
                    "Cameras: mirrorless, DSLR, point-and-shoot cameras, and action cameras.",
            ),
            Coupon(
                token = "C098",
                description =
                    "Lenses: prime, zoom, wide-angle, and telephoto lenses for photography.",
            ),
            Coupon(
                token = "C099",
                description = "Tripods and monopods: stable supports for cameras and smartphones.",
            ),
            Coupon(
                token = "C100",
                description =
                    "Studio lighting: softboxes, ring lights, LED panels, and light stands.",
            ),
            Coupon(
                token = "C101",
                description =
                    "Microphones: condenser, dynamic, lavalier, and USB microphones for audio recording.",
            ),
            Coupon(
                token = "C102",
                description =
                    "Headphones: over-ear, in-ear, wireless, and noise-cancelling headphones.",
            ),
            Coupon(
                token = "C103",
                description =
                    "Speakers: Bluetooth, smart, portable, and home theater speakers for audio enjoyment.",
            ),
            Coupon(
                token = "C104",
                description =
                    "Projectors: home theater, portable, and business projectors for presentations and movies.",
            ),
            Coupon(
                token = "C105",
                description =
                    "Screen protectors: tempered glass, privacy, and anti-glare screen protectors for devices.",
            ),
            Coupon(
                token = "C106",
                description =
                    "Phone cases: clear, rugged, wallet, and designer phone cases for protection.",
            ),
            Coupon(
                token = "C107",
                description =
                    "Chargers and cables: fast chargers, wireless chargers, USB-C, and Lightning cables.",
            ),
            Coupon(
                token = "C108",
                description =
                    "Power banks: portable chargers, high capacity, and fast charging power banks.",
            ),
            Coupon(
                token = "C109",
                description =
                    "Car mounts: phone holders, tablet mounts, and dashboard mounts for vehicles.",
            ),
            Coupon(
                token = "C110",
                description =
                    "Dash cams: front-facing, dual-channel, and parking mode dash cameras for safety.",
            ),
            Coupon(
                token = "C111",
                description =
                    "Car audio: car stereos, speakers, subwoofers, and amplifiers for enhanced sound.",
            ),
            Coupon(
                token = "C112",
                description =
                    "GPS navigators: in-car GPS devices, and navigation apps for accurate directions.",
            ),
            Coupon(
                token = "C113",
                description =
                    "Road trip essentials: cooler bags, travel mugs, car organizers, and blankets.",
            ),
            Coupon(
                token = "C114",
                description =
                    "Emergency kits: first aid, roadside assistance, and survival kits for unexpected situations.",
            ),
            Coupon(
                token = "C115",
                description =
                    "Pet beds: orthopedic, cooling, heated, and bolster pet beds for comfort.",
            ),
            Coupon(
                token = "C116",
                description =
                    "Pet collars: leather, nylon, reflective, and smart pet collars with GPS.",
            ),
            Coupon(
                token = "C117",
                description =
                    "Pet leashes: retractable, hands-free, and training leashes for walks.",
            ),
            Coupon(
                token = "C118",
                description =
                    "Pet toys: squeaky, chew, puzzle, and interactive pet toys for entertainment.",
            ),
            Coupon(
                token = "C119",
                description =
                    "Pet food dispensers: automatic, gravity, and smart pet food dispensers.",
            ),
            Coupon(
                token = "C120",
                description =
                    "Aquariums: fish tanks, filters, heaters, and decorations for aquatic pets.",
            ),
        )

    @JvmStatic
    fun testCases() =
        Stream.of(
            // Basic Exact Matches
            Arguments.of(
                SearchTestCase(
                    q = "fashion",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C001"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "tools",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C002", "C012", "C015"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "books",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C003", "C041", "C093"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "electronics",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C004", "C027"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "food",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C005", "C031", "C071", "C119"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "travel",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C006", "C046", "C058", "C113"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "health",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C007", "C096"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "decor",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C008", "C015", "C020", "C032", "C120"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "toys",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C009", "C037", "C053", "C054", "C118"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "pet",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons =
                        listOf("C010", "C047", "C115", "C116", "C117", "C118", "C119", "C120"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "sports",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C011", "C055"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "parts",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C012"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "art",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C008", "C013", "C032", "C052"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "musical",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C014", "C042"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "garden",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C015", "C040"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "jewelry",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C016"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "beauty",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C017"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "office",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C018", "C064", "C065", "C088"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "cleaning",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C019", "C045", "C063"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "party",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C020", "C039", "C066", "C067"),
                )
            ),

            // Minor Misspellings (Fuzzy specific)
            Arguments.of(
                SearchTestCase(
                    q = "tols",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C002", "C012", "C015"),
                )
            ), // tools
            Arguments.of(
                SearchTestCase(
                    q = "bokks",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C003", "C041", "C093"),
                )
            ), // books
            Arguments.of(
                SearchTestCase(
                    q = "electonics",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C004", "C027"),
                )
            ), // electronics
            Arguments.of(
                SearchTestCase(
                    q = "foos",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C005", "C031", "C071", "C119"),
                )
            ), // food
            Arguments.of(
                SearchTestCase(
                    q = "tavel",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C006", "C046", "C058", "C113"),
                )
            ), // travel
            Arguments.of(
                SearchTestCase(
                    q = "helth",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C007", "C096"),
                )
            ), // health
            Arguments.of(
                SearchTestCase(
                    q = "decot",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C008", "C015", "C020", "C032", "C120"),
                )
            ), // decor
            Arguments.of(
                SearchTestCase(
                    q = "toyz",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C009", "C037", "C053", "C054", "C118"),
                )
            ), // toys
            Arguments.of(
                SearchTestCase(
                    q = "pte",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons =
                        listOf("C010", "C047", "C115", "C116", "C117", "C118", "C119", "C120"),
                )
            ), // pet
            Arguments.of(
                SearchTestCase(
                    q = "spports",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C011", "C055"),
                )
            ), // sports
            Arguments.of(
                SearchTestCase(
                    q = "prats",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C012"),
                )
            ), // parts
            Arguments.of(
                SearchTestCase(
                    q = "artt",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C008", "C013", "C032", "C052"),
                )
            ), // art
            Arguments.of(
                SearchTestCase(
                    q = "muscal",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C014", "C042"),
                )
            ), // musical
            Arguments.of(
                SearchTestCase(
                    q = "gardn",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C015", "C040"),
                )
            ), // garden
            Arguments.of(
                SearchTestCase(
                    q = "jewlry",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C016"),
                )
            ), // jewelry
            Arguments.of(
                SearchTestCase(
                    q = "bauty",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C017"),
                )
            ), // beauty
            Arguments.of(
                SearchTestCase(
                    q = "offfice",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C018", "C064", "C065", "C088"),
                )
            ), // office
            Arguments.of(
                SearchTestCase(
                    q = "clening",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C019", "C045", "C063"),
                )
            ), // cleaning
            Arguments.of(
                SearchTestCase(
                    q = "partyy",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C020", "C039", "C066", "C067"),
                )
            ),

            // More Fuzzy Cases
            Arguments.of(
                SearchTestCase(
                    q = "wimen",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C001"),
                )
            ), // women
            Arguments.of(
                SearchTestCase(
                    q = "accesories",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons =
                        listOf(
                            "C001",
                            "C002",
                            "C010",
                            "C012",
                            "C046",
                            "C086",
                            "C090",
                            "C091",
                            "C095",
                            "C097",
                            "C098",
                            "C106",
                            "C107",
                            "C109",
                        ),
                )
            ), // accessories
            Arguments.of(
                SearchTestCase(
                    q = "gadgets",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C002", "C023", "C035", "C063"),
                )
            ), // gadgets
            Arguments.of(
                SearchTestCase(
                    q = "elgant",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C001", "C016"),
                )
            ), // elegant
            Arguments.of(
                SearchTestCase(
                    q = "nutrials",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C003"),
                )
            ), // materials
            Arguments.of(
                SearchTestCase(
                    q = "laptops",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C004"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "vegtabls",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C005", "C072", "C073"),
                )
            ), // vegetables
            Arguments.of(
                SearchTestCase(
                    q = "fruts",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C005", "C073"),
                )
            ), // fruits
            Arguments.of(
                SearchTestCase(
                    q = "vitamins",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C007", "C043"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "supplemnts",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C007", "C043"),
                )
            ), // supplements
            Arguments.of(
                SearchTestCase(
                    q = "furnitur",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C008", "C027", "C051"),
                )
            ), // furniture
            Arguments.of(
                SearchTestCase(
                    q = "beddingg",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C008"),
                )
            ), // bedding
            Arguments.of(
                SearchTestCase(
                    q = "eductional",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C003", "C009", "C037"),
                )
            ), // educational
            Arguments.of(
                SearchTestCase(
                    q = "builing",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C009"),
                )
            ), // building
            Arguments.of(
                SearchTestCase(
                    q = "runing",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C011"),
                )
            ), // running
            Arguments.of(
                SearchTestCase(
                    q = "shoos",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C011", "C021", "C022", "C057"),
                )
            ), // shoes
            Arguments.of(
                SearchTestCase(
                    q = "vehcles",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C012"),
                )
            ), // vehicles
            Arguments.of(
                SearchTestCase(
                    q = "paints",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C013", "C052"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "brishes",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C013", "C019", "C060", "C061"),
                )
            ), // brushes
            Arguments.of(
                SearchTestCase(
                    q = "keybords",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C014", "C086", "C090"),
                )
            ), // keyboards
            Arguments.of(
                SearchTestCase(
                    q = "ringss",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C016"),
                )
            ), // rings
            Arguments.of(
                SearchTestCase(
                    q = "skincaer",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C017", "C059"),
                )
            ), // skincare
            Arguments.of(
                SearchTestCase(
                    q = "makeupp",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C017", "C061"),
                )
            ), // makeup
            Arguments.of(
                SearchTestCase(
                    q = "printerss",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C018", "C087"),
                )
            ), // printers
            Arguments.of(
                SearchTestCase(
                    q = "vacuums",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C019", "C063"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "baloons",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C020", "C039"),
                )
            ), // balloons
            Arguments.of(
                SearchTestCase(
                    q = "costumes",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C020", "C066"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "sweaterrs",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C021"),
                )
            ), // sweaters
            Arguments.of(
                SearchTestCase(
                    q = "swits",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C022"),
                )
            ), // suits
            Arguments.of(
                SearchTestCase(
                    q = "drones",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C023", "C091"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "bred",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C024"),
                )
            ), // bread
            Arguments.of(
                SearchTestCase(
                    q = "blenders",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C025", "C035", "C081"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "vintag",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C027"),
                )
            ), // vintage
            Arguments.of(
                SearchTestCase(
                    q = "gamng",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C028", "C090", "C092"),
                )
            ), // gaming
            Arguments.of(
                SearchTestCase(
                    q = "fitnes",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C007", "C029", "C043", "C096"),
                )
            ), // fitness
            Arguments.of(
                SearchTestCase(
                    q = "campng",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C030", "C056"),
                )
            ), // camping
            Arguments.of(
                SearchTestCase(
                    q = "diapers",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C031"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "artwrok",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C032"),
                )
            ), // artwork
            Arguments.of(
                SearchTestCase(
                    q = "coffea",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C033", "C081"),
                )
            ), // coffee
            Arguments.of(
                SearchTestCase(
                    q = "teas",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C034"),
                )
            ), // tea
            Arguments.of(
                SearchTestCase(
                    q = "photo",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C036", "C097", "C098", "C099"),
                )
            ), // photography
            Arguments.of(
                SearchTestCase(
                    q = "scince",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C037"),
                )
            ), // science
            Arguments.of(
                SearchTestCase(
                    q = "essentials",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C017", "C031", "C081", "C025", "C114"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "relaxaton",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C038"),
                )
            ), // relaxation
            Arguments.of(
                SearchTestCase(
                    q = "baners",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C039"),
                )
            ), // banners
            Arguments.of(
                SearchTestCase(
                    q = "fertilizers",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C040"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "fiction",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C041"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "pianos",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C042"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "proteins",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C043"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "securty",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C044", "C083"),
                )
            ), // security
            Arguments.of(
                SearchTestCase(
                    q = "reuseable",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C045"),
                )
            ), // reusable
            Arguments.of(
                SearchTestCase(
                    q = "luggage",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C046"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "shampoos",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C047"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "beerr",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C048"),
                )
            ), // beer
            Arguments.of(
                SearchTestCase(
                    q = "wine",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C049"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "flourss",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C050"),
                )
            ), // flours
            Arguments.of(
                SearchTestCase(
                    q = "pation",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C051"),
                )
            ), // patio
            Arguments.of(
                SearchTestCase(
                    q = "watercolors",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C052"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "boardd",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C053"),
                )
            ), // board
            Arguments.of(
                SearchTestCase(
                    q = "puzzels",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C054"),
                )
            ), // puzzles
            Arguments.of(
                SearchTestCase(
                    q = "footbal",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C055"),
                )
            ), // football
            Arguments.of(
                SearchTestCase(
                    q = "tentss",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C030", "C056"),
                )
            ), // tents
            Arguments.of(
                SearchTestCase(
                    q = "hikng",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C057", "C058"),
                )
            ), // hiking
            Arguments.of(
                SearchTestCase(
                    q = "bakpacks",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C058"),
                )
            ), // backpacks
            Arguments.of(
                SearchTestCase(
                    q = "skincaer",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C017", "C059"),
                )
            ), // skincare (re-test)
            Arguments.of(
                SearchTestCase(
                    q = "dryers",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C060"),
                )
            ), // dryers
            Arguments.of(
                SearchTestCase(
                    q = "palettes",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C061"),
                )
            ), // palettes
            Arguments.of(
                SearchTestCase(
                    q = "perfumes",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C017", "C047", "C062"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "robott",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C063"),
                )
            ), // robot
            Arguments.of(
                SearchTestCase(
                    q = "organizers",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C064", "C079", "C113"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "noteboks",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C065"),
                )
            ), // notebooks
            Arguments.of(
                SearchTestCase(
                    q = "hallowen",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C066"),
                )
            ), // halloween
            Arguments.of(
                SearchTestCase(
                    q = "snacs",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C020", "C067"),
                )
            ), // snacks
            Arguments.of(
                SearchTestCase(
                    q = "juices",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C068"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "cereals",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C069"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "milks",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C070"),
                )
            ), // milk
            Arguments.of(
                SearchTestCase(
                    q = "chicken",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C071"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "pizzas",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C072"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "soups",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C073"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "spices",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C074", "C079"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "mixing",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C075"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "cookwar",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C076"),
                )
            ), // cookware
            Arguments.of(
                SearchTestCase(
                    q = "plates",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C077"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "glasses",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C078"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "storag",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C064", "C079", "C089"),
                )
            ), // storage
            Arguments.of(
                SearchTestCase(
                    q = "cakess",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C024", "C050", "C080"),
                )
            ), // cakes
            Arguments.of(
                SearchTestCase(
                    q = "toaster",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C025", "C081"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "refrigeators",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C082"),
                )
            ), // refrigerators
            Arguments.of(
                SearchTestCase(
                    q = "camerass",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C036", "C044", "C083", "C097", "C110"),
                )
            ), // cameras
            Arguments.of(
                SearchTestCase(
                    q = "smarts",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons =
                        listOf(
                            "C004",
                            "C023",
                            "C035",
                            "C044",
                            "C084",
                            "C095",
                            "C096",
                            "C116",
                            "C119",
                        ),
                )
            ), // smart
            Arguments.of(
                SearchTestCase(
                    q = "routers",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C085"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "keyboards",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C014", "C086", "C090"),
                )
            ), // keyboards (exact)
            Arguments.of(
                SearchTestCase(
                    q = "printerss",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C018", "C087"),
                )
            ), // printers (re-test)
            Arguments.of(
                SearchTestCase(
                    q = "softwar",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C088"),
                )
            ), // software
            Arguments.of(
                SearchTestCase(
                    q = "hard",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C089"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "headsets",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C023", "C086", "C090", "C102"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "drones",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C023", "C091"),
                )
            ), // drones (exact)
            Arguments.of(
                SearchTestCase(
                    q = "virtal",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C023", "C092"),
                )
            ), // virtual
            Arguments.of(
                SearchTestCase(
                    q = "kindl",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C093"),
                )
            ), // kindle
            Arguments.of(
                SearchTestCase(
                    q = "tablets",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C094", "C109"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "phones",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C004", "C095", "C106", "C109"),
                )
            ), // phones
            Arguments.of(
                SearchTestCase(
                    q = "wearable",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C096"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "lenses",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C036", "C098"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "tripods",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C036", "C099"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "lightingg",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C036", "C100", "C084"),
                )
            ), // lighting
            Arguments.of(
                SearchTestCase(
                    q = "micphones",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C101"),
                )
            ), // microphones
            Arguments.of(
                SearchTestCase(
                    q = "headpones",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C102"),
                )
            ), // headphones
            Arguments.of(
                SearchTestCase(
                    q = "spekaers",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C103", "C111"),
                )
            ), // speakers
            Arguments.of(
                SearchTestCase(
                    q = "projecors",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C104"),
                )
            ), // projectors
            Arguments.of(
                SearchTestCase(
                    q = "scren",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C105"),
                )
            ), // screen
            Arguments.of(
                SearchTestCase(
                    q = "chargers",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C095", "C107", "C108"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "powers",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C108"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "mouns",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C109"),
                )
            ), // mounts
            Arguments.of(
                SearchTestCase(
                    q = "dash",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C110", "C109"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "stereos",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C111"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "gps",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C112", "C116"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "road",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C113"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "emergency",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C114"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "beds",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C115"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "collars",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C116"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "leashes",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C117"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "toys",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C009", "C037", "C053", "C054", "C118"),
                )
            ), // toys (re-test)
            Arguments.of(
                SearchTestCase(
                    q = "dispensers",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C119"),
                )
            ),
            Arguments.of(
                SearchTestCase(
                    q = "aquariums",
                    coupons = couponsForFuzzySearch,
                    expectedCoupons = listOf("C120"),
                )
            ),
        )
}
