package de.herk.kfzd.data.loader

import android.content.Context
import de.herk.kfzd.data.model.PlateEntry
import de.herk.kfzd.data.model.PlateType
import java.io.InputStream

class AuthoritySeriesPlateAssetLoader(private val context: Context) {
    fun load(): List<PlateEntry> = context.assets.open(ASSET_PATH).use(::parse)

    companion object {
        const val ASSET_PATH = "data/german_authority_series.json"

        fun parse(input: InputStream): List<PlateEntry> =
            PlateAssetJsonReaderFactory.parse(input, PlateType.STATE_POLICE)
    }
}