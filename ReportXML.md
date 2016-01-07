# Introduction #

Currently, reports for ActivityInfo/Sigmah must be defined in XML.
The canonical source for documentation is the [Java sources](http://code.google.com/p/sigma-h/source/browse/trunk/sigmah/src/main/java/org/sigmah/shared/report/model/Report.java), but there is also an [XML Schema](http://sigma-h.googlecode.com/svn-history/r542/trunk/doc/reports/reports.xsd) available.

# Example #

[View the PDF](http://sigma-h.googlecode.com/svn-history/r542/trunk/doc/reports/Example-Ituri.pdf)

```
<report frequency="Adhoc">

    <filter>
        <restriction dimension="database">
            <category>1</category>
        </restriction>
        <restriction dimension="AdminLevel">
            <category>141831</category>
        </restriction>
    </filter>

    <title>Rapport Ituri</title>

    <description>Tableaux, graphiques et cartes pour une period données pour la district d'Ituri</description>

    <elements>
       

        <table>
            <title>Evaluations ménées</title>
            <filter>
                <restriction dimension="activity">
                    <category>24</category>
                </restriction>
            </filter>
            <columns frozen="4">
                <column label="Carte" source="map"/>
                <column label="Date" source="site.date1"/>
                <column label="Territoire" source="admin" sourceId="3"/>
                <column label="Site" source="location.name"/>
                <column label="Axe" source="location.axe"/>
                <column label="Commentaire" source="site.comments"/>
            </columns>
            <map>
                <baseMap>ituri.cd</baseMap>
                <layers>
                    <bubbles>
                        <numbering>arabicNumerals</numbering>
                        <minRadius>10</minRadius>
                    </bubbles>
                </layers>
            </map>
        </table>

        <table>
            <title>Distributions NFI</title>
            <filter>
                <restriction dimension="activity">
                    <category>22</category>
                </restriction>
            </filter>
            <columns>
                <column label="Carte" source="map"/>
                <column label="Période" source="site.date1"/>
                <column label="Sites d'Intevention" source="location.name"/>
                <column label="Territoire" source="admin" sourceId="3"/>
                <column label="Distribution effective NFI">
                    <column label="# de kits distribués" source="indicator" sourceId="28"/>
                </column>
                <column label="Commentaire" source="site.comments"/>
            </columns>
 <map>
            <baseMap>ituri.cd</baseMap>
            <layers>
                <bubbles>
                   <pie>true</pie>
                    <numbering>arabic</numbering>
                    <scaling>Graduated</scaling>
                    <indicators>
                        <indicator>29</indicator> <!-- Deplaces -->
                        <indicator>31</indicator> <!-- Retournees -->
                        <indicator>33</indicator> <!-- Fam d'acc -->
                        <indicator>34</indicator> <!-- catostroph -->
                    </indicators>
                    <colors>
                        <dimension type="indicator">
                            <category name="29" color="#FF0000" label="Déplacés"/>
                            <category name="31" color="#0000BB" label="Retournés"/>
                            <category name="33" color="#00BB00" label="Famille d'Accueille"/>
                            <category name="34" color="#FFFF00" label="Catostrophe Naturelle"/>
                        </dimension>
                    </colors>
                </bubbles>
            </layers>
        </map>

        </table>

       

        <pivotChart>
            <title>Répartition de Bénéficiaries par Statut</title>
            <type>Pie</type>
            <filter>
                <restriction dimension="indicator">
                    <category>29</category> <!-- Deplaces -->
                    <category>31</category> <!-- Retournees -->
                    <category>33</category> <!-- Fam d'acc -->
                    <category>34</category> <!-- catostroph -->
                </restriction>
            </filter>
            <categories>
                <dimension type="indicator">
                    <category name="29" color="#FF0000" label="Déplacés"/>
                    <category name="31" color="#0000BB" label="Retournés"/>
                    <category name="33" color="#00BB00" label="Famille d'Accueille"/>
                    <category name="34" color="#FFFF00" label="Catostrophe Naturelle"/>
                </dimension>
            </categories>
        </pivotChart>

        <pivotChart>
            <title>Répartition de Bénéficiaries par Territoire et Position</title>
            <type>StackedBar</type>
            <filter>
                <restriction dimension="indicator">
                    <category>29</category> <!-- Deplaces -->
                    <category>31</category> <!-- Retournees -->
                    <category>33</category> <!-- Fam d'acc -->
                    <category>34</category> <!-- catostroph -->
                </restriction>
            </filter>
            <categories>
                <dimension type="admin" levelId="3"/>
            </categories>
            <series>
                <dimension type="indicator">
                    <category name="29" color="#FF0000" label="Déplacés"/>
                    <category name="31" color="#0000BB" label="Retournés"/>
                    <category name="33" color="#00BB00" label="Famille d'Accueille"/>
                    <category name="34" color="#FFFF00" label="Catostrophe Naturelle"/>
                </dimension>
            </series>
        </pivotChart>

        <pivotChart>
            <title>Nombre de kits distribués par mois</title>
            <type>Line</type>
            <filter>
                <restriction dimension="indicator">
                    <category>28</category> <!-- Total kits -->
                </restriction>
            </filter>
            <categories>
                <dimension type="date" dateUnit="month"/>
            </categories>
        </pivotChart>



        <table>
            <title>Tableau récapitulatif des Interventions EHA</title>
            <filter>
                <restriction dimension="activity">
                    <category>28</category>
                </restriction>
            </filter>
            <columns>
                <column label="Carte" source="map"/>
                <column label="Périod">

<column label="De" source="site.date1"/>
<column label="À" source="site.date2"/>
</column>
                <column label="Site d'Intervention">
                    <column label="Sites" source="location.name"/>
                    <column label="Territorie" source="admin" sourceId="3"/>
                    <!--<column label="Cause"/>-->
                    <column label="# latrines construites (portes)" source="indicator" sourceId="76"/>
                    <column label="# douches (portes)" source="indicator" sourceId="79"/>
                    <column label="# kits hygiène distribués" source="indicator" sourceId="82"/>
                </column>
                 <column label="Commentaire" source="site.comments"/>
            </columns>


        <map>
            <baseMap>zs.gray.cd</baseMap>
            <layers>
                <bubbles>
                    <numbering>arabic</numbering>
                    <minRadius>15</minRadius>
                    <maxRadius>30</maxRadius>
                    <scaling>Graduated</scaling>
                    <pie>true</pie>
                    <indicators>
                        <indicator>100</indicator>
                        <indicator>103</indicator>
                        <indicator>102</indicator>
                        <indicator>104</indicator>
                        <indicator>101</indicator>
                    </indicators>
                    <colors>
                        <dimension type="indicator">
                            <category name="100" color="#FFFF00" label="Water-trucking"/>
                            <category name="103" color="#254061" label="Eau durable"/>
                            <category name="102" color="#4F81BD" label="Eau d'urgence"/>
                            <category name="104" color="#77933C" label="Assainissement durable"/>
                            <category name="101" color="#C3D69B" label="Assainissement d'urgence"/>
                        </dimension>
                    </colors>
                </bubbles>
            </layers>
        </map>
        </table>

        <pivotChart>
            <title>Répartion Béneficiares par type d'intervention</title>
            <type>Pie</type>
            <filter>
                <restriction dimension="indicator">
                    <category>100</category>   <!-- Water Trucking -->
                    <category>101</category>   <!-- Nombre de personnes beneficiares d'assanissment d'urgence -->
                    <category>102</category>   <!-- Nombre de personnes bénéficiaries de l'eau d'urgence -->
                    <category>103</category>   <!--Nombre de personnes bénéficiaries de l'eau durable -->
                    <category>104</category>  <!-- Nombre de personnes bénéficiaries d'assainissement durable -->
                </restriction>
            </filter>
            <categories>
                <dimension type="indicator">
                    <category name="100" color="#FFFF00" label="Water-trucking"/>
                    <category name="103" color="#254061" label="Eau durable"/>
                    <category name="102" color="#4F81BD" label="Eau d'urgence"/>
                    <category name="104" color="#77933C" label="Assainissement durable"/>
                    <category name="101" color="#C3D69B" label="Assainissement d'urgence"/>
                </dimension>
            </categories>
        </pivotChart>

        <pivotChart>
            <title>Répartion Béneficiares par type d'intervention</title>
            <type>StackedBar</type>
            <filter>
                <restriction dimension="indicator">
                    <category>100</category>   <!-- Water Trucking -->
                    <category>101</category>   <!-- Nombre de personnes beneficiares d'assanissment d'urgence -->
                    <category>102</category>   <!-- Nombre de personnes bénéficiaries de l'eau d'urgence -->
                    <category>103</category>   <!--Nombre de personnes bénéficiaries de l'eau durable -->
                    <category>104</category>  <!-- Nombre de personnes bénéficiaries d'assainissement durable -->
                </restriction>
            </filter>
            <categories>
                <dimension type="admin" levelId="2"/>
            </categories>
            <series>
                <dimension type="indicator">
                    <category name="100" color="#FFFF00" label="Water-trucking"/>
                    <category name="103" color="#254061" label="Eau durable"/>
                    <category name="102" color="#4F81BD" label="Eau d'urgence"/>
                    <category name="104" color="#77933C" label="Assainissement durable"/>
                    <category name="101" color="#C3D69B" label="Assainissement d'urgence"/>
                </dimension>
            </series>
        </pivotChart>

        <pivotChart>
            <title>Nombre de Béneficiares par Mois</title>
            <type>Line</type>
            <filter>
                <restriction dimension="indicator">
                    <category>100</category>   <!-- Water Trucking -->
                    <category>101</category>   <!-- Nombre de personnes beneficiares d'assanissment d'urgence -->
                    <category>102</category>   <!-- Nombre de personnes bénéficiaries de l'eau d'urgence -->
                    <category>103</category>   <!--Nombre de personnes bénéficiaries de l'eau durable -->
                    <category>104</category>  <!-- Nombre de personnes bénéficiaries d'assainissement durable -->
                </restriction>
            </filter>
            <categories>
                <dimension type="date" dateUnit="month"/>
            </categories>
            <series>
                <dimension type="indicator">
                    <category name="100" color="#FFFF00" label="Water-trucking"/>
                    <category name="103" color="#254061" label="Eau durable"/>
                    <category name="102" color="#4F81BD" label="Eau d'urgence"/>
                    <category name="104" color="#77933C" label="Assainissement durable"/>
                    <category name="101" color="#C3D69B" label="Assainissement d'urgence"/>
                </dimension>
            </series>
        </pivotChart>

<map>
<title>Nombre des éléves bénéficiantes d'une assistance en Education d'Urgence</title>
            <baseMap>ituri.cd</baseMap>
            <layers>
                <bubbles>
                    <scaling>Graduated</scaling>
                    <indicators>
                        <indicator>50</indicator> <!-- Eleves total -->
                    </indicators>
        
                </bubbles>
            </layers>
        </map>

  <pivotChart>
            <title>Nombre de Béneficiares par Mois</title>
            <type>Line</type>
            <filter>
                <restriction dimension="indicator">
                    <category>50</category>
                </restriction>
            </filter>
            <categories>
                <dimension type="date" dateUnit="month"/>
            </categories>
            <series>
                <dimension type="indicator">
                   <category name="50" color="#38A800" label="Eleves"/>
                </dimension>
            </series>
        </pivotChart>
 <pivotTable>
            <title>Annexe: Résumé de résultats globales</title>
            <sheetTitle>Totals</sheetTitle>

            <rows>
                <dimension type="activity"/>
                <dimension type="indicator"/>
            </rows>
            <columns>
                <dimension type="admin" levelId="1"/>
                <dimension type="partner"/>
            </columns>
        </pivotTable>

    </elements>
</report>
```